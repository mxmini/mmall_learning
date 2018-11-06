package person.mmall.service;

import person.mmall.commom.ServerResponse;

public interface ICartService {

    ServerResponse cartList(Integer userId);

    ServerResponse addCart(Integer userId,Integer productId, Integer count);

    ServerResponse updateCart(Integer userId,Integer productId, Integer count);

    ServerResponse removeProductFromCart(Integer userId, String productIds);

    ServerResponse selectProduct(Integer userId, Integer productId);

    ServerResponse unSelectProduct(Integer userId, Integer productId) ;

    ServerResponse queryCartProductCount(Integer userId);

    ServerResponse selectAllProductAtCart(Integer userId);

    ServerResponse unSelectAllProductAtCart(Integer userId);
}
