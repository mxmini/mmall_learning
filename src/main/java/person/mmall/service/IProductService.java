package person.mmall.service;

import person.mmall.commom.ServerResponse;
import person.mmall.pojo.Product;

public interface IProductService {

    ServerResponse productDetail(Integer productId);

    ServerResponse searchProduct(Integer categoryId, String keyWord, Integer pageNum, Integer pageSize, String orderBy);

    //manage product at follow
    ServerResponse getProductList(Integer pageNum, Integer pageSize);

    ServerResponse getSearchProductList(Integer pageNum, Integer pageSize, Integer productId, String productName);

    ServerResponse getProductInformation(Integer productId);

    ServerResponse setProductStatus(Integer productId, Integer status);

    ServerResponse addOrUpdateProduct(Product product);
}
