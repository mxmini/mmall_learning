package person.mmall.dao;

        import org.apache.ibatis.annotations.Param;
        import person.mmall.pojo.Cart;

        import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    List<Cart> selectByUserId(Integer userId);

    int selectCartProductCheckedStatusByUserId(Integer userId);

    Cart selectByUserIdWithProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    int deleteByUserIdWithProductId(@Param("userId") Integer userId, @Param("productId") List<String> productId);

    List<Cart> selectByUserWhenChecked(Integer userId);
}