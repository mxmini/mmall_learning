package person.mmall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.dao.CartMapper;
import person.mmall.dao.ProductMapper;
import person.mmall.pojo.Cart;
import person.mmall.pojo.Product;
import person.mmall.service.ICartService;
import person.mmall.utils.BigDecimalUtil;
import person.mmall.utils.PropertiesUtil;
import person.mmall.valueobject.CartProductVo;
import person.mmall.valueobject.CartVo;

import java.math.BigDecimal;
import java.util.List;

@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse unSelectAllProductAtCart(Integer userId){

        List<Cart> cartList = cartMapper.selectByUserId(userId);

        for (Cart cart : cartList) {
            if (cart.getChecked() != Constant.Cart.UN_CHECKED){
                cart.setChecked(Constant.Cart.UN_CHECKED);
                cartMapper.updateByPrimaryKeySelective(cart);
            }
        }

        return this.cartList(userId);
    }

    public ServerResponse selectAllProductAtCart(Integer userId){

        List<Cart> cartList = cartMapper.selectByUserId(userId);

        for (Cart cart : cartList) {
            if (cart.getChecked() != Constant.Cart.CHECKED){
                cart.setChecked(Constant.Cart.CHECKED);
                cartMapper.updateByPrimaryKeySelective(cart);
            }
        }

        return this.cartList(userId);
    }

    public  ServerResponse queryCartProductCount(Integer userId){

        Integer productCount = 0;
        List<Cart> cartList = cartMapper.selectByUserId(userId);

        for (Cart cart : cartList)
            productCount += cart.getQuantity();

        return ServerResponse.CreateBySuccess(productCount);
    }

    public ServerResponse unSelectProduct(Integer userId, Integer productId) {

        if (null == productId)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Cart cart = cartMapper.selectByUserIdWithProductId(userId, productId);
        if (null == cart)
            return ServerResponse.CreateByErrorMessage("购物车产品不存在");

        cart.setChecked(Constant.Cart.UN_CHECKED);
        cartMapper.updateByPrimaryKeySelective(cart);

        return this.cartList(userId);
    }

    public ServerResponse selectProduct(Integer userId, Integer productId){

        if(null == productId){
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectByUserIdWithProductId(userId, productId);
        if (null == cart){
            return ServerResponse.CreateByErrorMessage("购物车产品不存在");
        }

        cart.setChecked(Constant.Cart.CHECKED);
        cartMapper.updateByPrimaryKeySelective(cart);

        return this.cartList(userId);
    }

    public ServerResponse removeProductFromCart(Integer userId, String productIds){

        List<String> productList = Splitter.on(",").splitToList(productIds);
        if (org.springframework.util.CollectionUtils.isEmpty(productList)){
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

            cartMapper.deleteByUserIdWithProductId(userId,productList);

        return this.cartList(userId);
    }

    public ServerResponse updateCart(Integer userId,Integer productId, Integer count){

        if (null == productId || null == count)
            return ServerResponse.CreateByErrorMessage("参数错误");

        Product product = productMapper.selectByPrimaryKey(productId);
        if (null == product)
            return ServerResponse.CreateByErrorMessage("产品不存在");

        if (product.getStatus() == Constant.ProductStatusEnum.ON_SALE.getCode()){
            Cart cartItem = cartMapper.selectByUserIdWithProductId(productId, userId);
            if (null == cartItem)
                return ServerResponse.CreateByErrorMessage("购物车产品不存在");
            cartItem.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cartItem);
        }

        return this.cartList(userId);
    }

    public ServerResponse addCart(Integer userId,Integer productId, Integer count){

        if (null == productId || null == count)
            return ServerResponse.CreateByErrorMessage("参数错误");

        //第一步：判断该商品是否存在再购物车
        //存在则在原有的基础上增加数量，不存在则新增
        //第二步：判断数量是否超出当前商品的库存

        Product product = productMapper.selectByPrimaryKey(productId);
        if (null == product)
            return ServerResponse.CreateByErrorMessage("产品不存在");
        if (product.getStatus() == Constant.ProductStatusEnum.ON_SALE.getCode()){
            Cart cartItem = cartMapper.selectByUserIdWithProductId(productId, userId);
            if (null == cartItem){
                Cart cart = new Cart();
                cart.setQuantity(count);
                cart.setUserId(userId);
                cart.setProductId(productId);
                cart.setChecked(Constant.Cart.CHECKED);
                cartMapper.insert(cart);
            }else{
                cartItem.setQuantity(cartItem.getQuantity() + count);
                cartMapper.updateByPrimaryKeySelective(cartItem);
            }
        }

        return this.cartList(userId);
    }

    public ServerResponse cartList(Integer userId){

        CartVo cartVo = getCartVoLimit(userId);

        return ServerResponse.CreateBySuccess(cartVo);
    }

    private CartVo getCartVoLimit(Integer userId){

        CartVo cartVo = new CartVo();

        List<Cart> cartList = cartMapper.selectByUserId(userId);

        BigDecimal cartTotalPrice = new BigDecimal("0");
        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        if (CollectionUtils.isNotEmpty(cartList)){
            for(Cart cartItem : cartList){
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cartItem.getId());
                cartProductVo.setUserId(cartItem.getUserId());
                cartProductVo.setProductId(cartItem.getProductId());

               Product product =  productMapper.selectByPrimaryKey(cartItem.getProductId());
               if (null != product){
                   cartProductVo.setProductMainImage(product.getMainImage());
                   cartProductVo.setProductName(product.getName());
                   cartProductVo.setProductSubtitle(product.getSubtitle());
                   cartProductVo.setProductStatus(product.getStatus());
                   cartProductVo.setProductPrice(product.getPrice());
                   cartProductVo.setProductStock(product.getStock());

                   //判断库存
                   int buyLimitCount = 0;

                   if (product.getStock() >= cartItem.getQuantity()){
                       //库存充足
                       buyLimitCount = cartItem.getQuantity();
                       cartProductVo.setLimitQuantity(Constant.Cart.LIMIT_NUM_SUCCESS);
                   }else{
                        buyLimitCount = product.getStock();
                       cartProductVo.setLimitQuantity(Constant.Cart.LIMIT_NUM_FAIL);
                       //更新购物车中的有效库存
                       Cart cartForQuantity = new Cart();
                       cartForQuantity.setId(cartItem.getId());
                       cartForQuantity.setQuantity(buyLimitCount);
                       cartMapper.updateByPrimaryKeySelective(cartForQuantity);
                   }
                   cartProductVo.setQuantity(buyLimitCount);
                   //计算总价
                   cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),cartProductVo.getQuantity()));
                   cartProductVo.setProductChecked(cartItem.getChecked());

                   if(cartItem.getChecked() == Constant.Cart.CHECKED){
                       //如果已经勾选,增加到整个的购物车总价中
                       cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),cartProductVo.getProductTotalPrice().doubleValue());
                   }
                   cartProductVoList.add(cartProductVo);
               }
            }
        }

        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));

        return  cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId){

        if (null == userId)
            return  false;

        return 0 == cartMapper.selectCartProductCheckedStatusByUserId(userId);
    }
}
