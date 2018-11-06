package person.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.dao.CategoryMapper;
import person.mmall.dao.ProductMapper;
import person.mmall.pojo.Category;
import person.mmall.pojo.Product;
import person.mmall.service.ICategoryService;
import person.mmall.service.IProductService;
import person.mmall.utils.DateTimeUtil;
import person.mmall.utils.PropertiesUtil;
import person.mmall.valueobject.ProductDetailsVo;
import person.mmall.valueobject.ProductListVo;

import java.util.List;

@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    public ServerResponse productDetail(Integer productId){

        if (null == productId)
            return ServerResponse.CreateBySuccessMessage("参数错误");

        Product product = productMapper.selectByPrimaryKey(productId);
        if (null == product)
            return ServerResponse.CreateByErrorMessage("产品不存在");

        return ServerResponse.CreateBySuccess(product);
    }

    public ServerResponse searchProduct(Integer categoryId, String keyWord, Integer pageNum, Integer pageSize, String orderBy){

        if (null == categoryId && StringUtils.isBlank(keyWord))
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        List<Integer> categoryIdList = Lists.newArrayList();
        if (null != categoryId){
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (null == category && StringUtils.isBlank(keyWord))
            {//没有该分类，并且也没有关键字，返回一个空集合，不报错
                PageHelper.startPage(pageSize, pageNum);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo result = new PageInfo(productListVoList);
                return ServerResponse.CreateBySuccess(result);
            }

            categoryIdList = iCategoryService.getCategoryAndChildrenById(categoryId).getData();
        }

        if (StringUtils.isNotBlank(keyWord)){
            keyWord = new StringBuilder().append("%").append(keyWord).append("%").toString();
        }

        PageHelper.startPage(pageSize, pageNum);
        if (StringUtils.isNotBlank(orderBy)){
            if (Constant.ProductOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderArray = orderBy.split("_");
                PageHelper.orderBy(orderArray[0] + " " + orderArray[1]);
            }
        }

        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyWord)?null:keyWord,categoryIdList.size()==0?null:categoryIdList);

        List<ProductListVo> productListVoList = Lists.newArrayList();
        for(Product product : productList){
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);

        return ServerResponse.CreateBySuccess(pageInfo);
    }


    //manager product at follow

    /**
     * 利用mybatis plugin  PageHelper 对商品进行分页显示
     * @param pageNum 当前页面
     * @param pageSize 划分个数
     * @return
     */
    public ServerResponse getProductList(Integer pageNum, Integer pageSize){

        //startPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾

        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList =productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
       for (Product productItem : productList){
        ProductListVo productListVo = assembleProductListVo(productItem);
        productListVoList.add(productListVo);
       }
        PageInfo result = new PageInfo(productList);
       result.setList(productListVoList);

       return ServerResponse.CreateBySuccess(result);
    }

    public ServerResponse getSearchProductList(Integer pageNum, Integer pageSize, Integer productId, String productName){

        productName = new StringBuilder().append("%").append(productName).append("%").toString();

        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectProductByIdOrName(productId, productName);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product productItem : productList){
            ProductListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }

        PageInfo result = new PageInfo(productList);
        result.setList(productListVoList);

        return ServerResponse.CreateBySuccess(result);
    }

    public ServerResponse getProductInformation(Integer productId){

        if (null == productId)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Product product = productMapper.selectByPrimaryKey(productId);

        if ( null == product)
            return ServerResponse.CreateByErrorMessage("商品已经被删除");

        return ServerResponse.CreateBySuccess(assembleProductVo(product));
    }

    public ServerResponse setProductStatus(Integer productId, Integer status){

        if (null == productId)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        int resultCount = productMapper.selectByProductId(productId);

        if (0 == resultCount )
            return ServerResponse.CreateByErrorMessage("商品已经被删除");

        Product product = new Product();

        product.setId(productId);
        product.setStatus(status);

        resultCount = productMapper.updateByPrimaryKeySelective(product);

        if ( 0 < resultCount)
            return ServerResponse.CreateBySuccessMessage("修改产品状态成功");

        return ServerResponse.CreateBySuccessMessage("修改产品状态失败");
    }

    public ServerResponse addOrUpdateProduct(Product product){

        //新增
        if (null == product.getId())
        {
            int resultCount = productMapper.insert(product);

            if (0 < resultCount)
                return ServerResponse.CreateBySuccessMessage("新增产品成功");
            return ServerResponse.CreateByErrorMessage("新增产品失败");
        }

        //修改
        int resultCount = productMapper.updateByPrimaryKey(product);

        if (0 < resultCount)
            return ServerResponse.CreateBySuccessMessage("更新产品成功");

        return ServerResponse.CreateByErrorMessage("更新产品失败");
    }

    private ProductDetailsVo assembleProductVo(Product product){

        ProductDetailsVo productDetailsVo = new ProductDetailsVo();

        productDetailsVo.setId(product.getId());
        productDetailsVo.setCategoryId(product.getCategoryId());
        productDetailsVo.setName(product.getName());
        productDetailsVo.setMainImage(product.getMainImage());
        productDetailsVo.setSubTitle(product.getSubtitle());
        productDetailsVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        productDetailsVo.setSubImage(product.getSubImages());
        productDetailsVo.setDetail(product.getDetail());
        productDetailsVo.setPrice(product.getPrice());
        productDetailsVo.setStock(product.getStock());
        productDetailsVo.setStatus(product.getStatus());
        productDetailsVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime(), "yyyy-MM-dd hh:mm::ss"));
        productDetailsVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime(), "yyyy-MM-dd hh:mm::ss"));


        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(null == category)
            productDetailsVo.setParentCategoryId(0);
        else
            productDetailsVo.setParentCategoryId(category.getParentId());

        return productDetailsVo;
    }

    private ProductListVo assembleProductListVo(Product product){

        ProductListVo productVo = new ProductListVo();

        productVo.setId(product.getId());
        productVo.setCategoryId(product.getCategoryId());
        productVo.setName(product.getName());
        productVo.setMainImage(product.getMainImage());
        productVo.setPrice(product.getPrice());
        productVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.happymmall.com/"));
        productVo.setStatus(product.getStatus());
        productVo.setSubTitle(product.getSubtitle());

        return productVo;
    }
}
