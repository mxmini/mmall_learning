package person.mmall.service;

import person.mmall.commom.ServerResponse;

public interface IOrderService {

    ServerResponse getAllList(Integer pageSize, Integer pageNum);

    ServerResponse searchOrder(Long orderNo, Integer pageSize, Integer pageNum);

    ServerResponse orderDetail(Long orderNo);

    ServerResponse sendGoods(Long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse getOrderCartProduct(Integer userId);

    ServerResponse orderList(Integer userId, Integer pageSize, Integer pageNum);

    ServerResponse cancelOrder(Integer userId, Long orderNo);

}
