package person.mmall.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.mmall.commom.ServerResponse;
import person.mmall.dao.CategoryMapper;
import person.mmall.pojo.Category;
import person.mmall.service.ICategoryService;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public ServerResponse<List<Category>> getLevelCategory(Integer categoryId) {

        List<Category> categoryList = categoryMapper.selectCategoryByParentId(categoryId);

        if (categoryList.isEmpty())
            return ServerResponse.CreateByErrorMessage("未找到该品类");

        return ServerResponse.CreateBySuccess(categoryList);
    }

    /**
     * 添加类目：可以添加子类目，也可以添加根类目
     * @param categoryId
     * @param categoryName
     * @return
     */
    public ServerResponse addCategory(Integer categoryId, String categoryName){

        if (categoryId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.CreateByErrorMessage("参数错误");

        Category category = new Category();
        category.setParentId(categoryId);
        category.setName(categoryName);
        category.setStatus(true);   //可以有子类目

         int resultCount = categoryMapper.insert(category);

        if (0 < resultCount)
            return ServerResponse.CreateBySuccessMessage("添加商品类目成功");

        return ServerResponse.CreateByErrorMessage("添加商品类目失败");
    }

    /**
     * 修改类目的id和类目的名称
     * @param categoryId
     * @param categoryName
     * @return
     */
    public ServerResponse setCategory(Integer categoryId, String categoryName){

        if (categoryId == null || StringUtils.isBlank(categoryName))
            return ServerResponse.CreateByErrorMessage("参数错误");

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

         int resultCount = categoryMapper.updateByPrimaryKeySelective(category);

        if ( 0 < resultCount)
            return ServerResponse.CreateBySuccessMessage("更新类目成功");

        return ServerResponse.CreateByErrorMessage("更新类目失败");
    }

    /**
     * 递归查找子类目
     * @param categorySet
     * @param parentId
     * @return
     */
    private void findChildCategory(Set<Category> categorySet, Integer parentId){

        Category category = categoryMapper.selectByPrimaryKey(parentId);
        if (category != null)
            categorySet.add(category);

        List<Category> categoryList = categoryMapper.selectCategoryByParentId(parentId);
        for (Category categoryItem: categoryList) {
            findChildCategory(categorySet, categoryItem.getId());

        }

        return ;
    }
    public  ServerResponse<Set<Category>> getLevelAndDeepCategory(Integer categoryId){

        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, categoryId);

        return ServerResponse.CreateBySuccess(categorySet);
    }

    public ServerResponse<List<Integer>> getCategoryAndChildrenById(Integer id){

        Set<Category> categorySet = Sets.newHashSet();
        findChildCategory(categorySet, id);

        List<Integer> categoryIdList = Lists.newArrayList();
        for (Category category: categorySet){
            categoryIdList.add(category.getId());
        }

        return ServerResponse.CreateBySuccess(categoryIdList);
    }


}
