package person.mmall.service.impl;

import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.mmall.commom.ServerResponse;
import person.mmall.dao.OrderMapper;
import person.mmall.dao.ProductMapper;
import person.mmall.dao.UserMapper;
import person.mmall.service.IStatisticService;

import java.util.Map;

@Service("iStatisticService")
public class StatisticServiceImpl implements IStatisticService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;

    public ServerResponse baseCount(){

        Map countMap = Maps.newHashMap();

        Integer resultCount = userMapper.countUser();
        if (0 == resultCount)
            return ServerResponse.CreateByErrorMessage("统计用户出错");

        countMap.put("userCount", resultCount);

        resultCount = productMapper.countProduct();
        if (0 == resultCount)
            return ServerResponse.CreateByErrorMessage("统计产品出错");

        countMap.put("productCount", resultCount);

        resultCount = orderMapper.countOrder();
        if (0 == resultCount)
            return ServerResponse.CreateByErrorMessage("统计订单出错");

        countMap.put("orderCount", resultCount);

        return ServerResponse.CreateBySuccess(countMap);
    }
}
