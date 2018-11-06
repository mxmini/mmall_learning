package person.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.mmall.commom.ServerResponse;
import person.mmall.dao.ShippingMapper;
import person.mmall.pojo.Shipping;
import person.mmall.service.IShippingService;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    public ServerResponse addShipping(Integer userId, Shipping shipping){

        shipping.setUserId(userId);

        int resultCount =shippingMapper.insert(shipping);
        if (0 < resultCount) {
            Map map = Maps.newHashMap();
            map.put("shippingId", shipping.getId());
            return ServerResponse.CreateBySuccess("添加地址成功", map);
        }

        return ServerResponse.CreateByErrorMessage("添加地址失败");
    }

    public ServerResponse delShipping(Integer userId, Integer shippingId){

        int resultCount = shippingMapper.deleteByUserIdWithShippingId(userId, shippingId);

        if (0 < resultCount)
            return ServerResponse.CreateBySuccessMessage("删除地址成功");

        return ServerResponse.CreateByErrorMessage("删除地址失败");
    }

    public ServerResponse updateShipping(Integer userId, Shipping shipping){

        shipping.setUserId(userId);
        int resultCount = shippingMapper.updateByPrimaryKeySelective(shipping);

        if (0 < resultCount)
            return ServerResponse.CreateByErrorMessage("更新地址成功");

        return ServerResponse.CreateByErrorMessage("更新地址失败");
    }

    public ServerResponse selectShipping(Integer userId, Integer shippingId){

        Shipping shipping = shippingMapper.selectByUserIdWithShippingId(userId, shippingId);
        if (null ==shipping)
            return ServerResponse.CreateByErrorMessage("查询地址失败");

        return ServerResponse.CreateBySuccess(shipping);
    }

    public ServerResponse shippingList(Integer userId, Integer pageSize, Integer pageNum){

        PageHelper.startPage(pageSize, pageNum);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);

        PageInfo pageInfo = new PageInfo(shippingList);

        return ServerResponse.CreateBySuccess(pageInfo);
    }
}
