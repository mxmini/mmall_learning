package person.mmall.service;

import person.mmall.commom.ServerResponse;
import person.mmall.pojo.Category;

import java.util.List;
import java.util.Set;


public interface ICategoryService {

     ServerResponse<List<Category>> getLevelCategory(Integer categoryId);

     ServerResponse addCategory(Integer categoryId, String categoryName);

     ServerResponse setCategory(Integer categoryId, String categoryName);

     ServerResponse<Set<Category>> getLevelAndDeepCategory(Integer categoryId);

}
