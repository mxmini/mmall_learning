package person.mmall.service;

import person.mmall.commom.ServerResponse;
import person.mmall.pojo.Product;

public interface IProductService {

    ServerResponse getProductList(Integer pageNum, Integer pageSize);

    ServerResponse getSearchProductList(Integer pageNum, Integer pageSize, Integer productId, String productName);

    ServerResponse getProductInformation(Integer productId);

    ServerResponse setProductStatus(Integer productId, Integer status);

    ServerResponse addOrUpdateProduct(Product product);
}
