package person.mmall.dao;

import org.apache.ibatis.annotations.Param;
import person.mmall.pojo.Shipping;

import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByUserIdWithShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    Shipping selectByUserIdWithShippingId(@Param("userId") Integer userId, @Param("shippingId") Integer shippingId);

    List<Shipping> selectByUserId(Integer userId);
}