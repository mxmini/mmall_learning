package person.mmall.dao;

import org.apache.ibatis.annotations.Param;
import person.mmall.pojo.Product;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectProductByIdOrName(@Param("productId") Integer productId, @Param("name") String name);

    int selectByProductId(Integer productId);
}