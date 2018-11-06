package person.mmall.dao;

import org.apache.ibatis.annotations.Param;
import person.mmall.pojo.OrderItem;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByorderNo(Long orderNo);

    List<OrderItem> selectByorderNoWithUserId(@Param("orderNo") Long orderNo, @Param("userId") Integer userId);

    int batchInsert(List<OrderItem> orderItemList);
}