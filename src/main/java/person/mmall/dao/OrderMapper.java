package person.mmall.dao;

import org.apache.ibatis.annotations.Param;
import person.mmall.pojo.Order;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Integer countOrder();

    List<Order> selectOrder();

    Order selectOrderByorderNo(Long orderNo);

    List<Order> selectByUserId(Integer UserId);

    Order selectOrderByorderNoAndUserId(@Param("userId")Integer UserId, @Param("orderNo") Long orderNo);

}