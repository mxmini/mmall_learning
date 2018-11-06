package person.mmall.service;

import person.mmall.commom.ServerResponse;
import person.mmall.pojo.Shipping;

public interface IShippingService {

    ServerResponse addShipping(Integer userId, Shipping shipping);

    ServerResponse delShipping(Integer userId, Integer shippingId);

    ServerResponse updateShipping(Integer userId, Shipping shipping);

    ServerResponse selectShipping(Integer userId, Integer shippingId);

    ServerResponse shippingList(Integer userId, Integer pageSize, Integer pageNum);

}
