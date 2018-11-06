package person.mmall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import person.mmall.commom.Constant;
import person.mmall.commom.ResponseCode;
import person.mmall.commom.ServerResponse;
import person.mmall.dao.*;
import person.mmall.pojo.*;
import person.mmall.service.IOrderService;
import person.mmall.utils.BigDecimalUtil;
import person.mmall.utils.PropertiesUtil;
import person.mmall.valueobject.OrderItemVo;
import person.mmall.valueobject.OrderProductVo;
import person.mmall.valueobject.OrderVo;
import person.mmall.valueobject.ShippingVo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private ProductMapper productMapper;

    public ServerResponse cancelOrder(Integer userId, Long orderNo){

        Order order =  orderMapper.selectOrderByorderNoAndUserId(userId, orderNo);
        if (null == order)
            return ServerResponse.CreateByErrorMessage("订单不存在");
        if (order.getStatus() != Constant.OrderStatusEnum.NO_PAY.getCode())
            return ServerResponse.CreateByErrorMessage("订单的状态异常");
        //更新订单状态
        order.setStatus(Constant.OrderStatusEnum.CANCELED.getCode());
        orderMapper.updateByPrimaryKeySelective(order);

        List<OrderItem> orderItemList = orderItemMapper.selectByorderNo(orderNo);

        //恢复产品的库存
        for (OrderItem orderItem : orderItemList) {
            Integer quantity = orderItem.getQuantity();
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() + quantity);
            productMapper.updateByPrimaryKeySelective(product);
        }

        return ServerResponse.CreateBySuccessMessage("取消订单成功");
    }
    public ServerResponse orderList(Integer userId, Integer pageSize, Integer pageNum){

        List<OrderVo> orderVoList = Lists.newArrayList();

        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        if (CollectionUtils.isEmpty(orderList))
            return ServerResponse.CreateByErrorMessage("订单为空");

        orderVoList = assembleOrderVoList(orderList, userId);
        PageInfo pageInfo = new PageInfo(orderList);
        pageInfo.setList(orderVoList);

        return ServerResponse.CreateBySuccess(pageInfo);
    }
    /**
     * 购物车页面跳转到确认创建订单页面
     * @param userId
     * @return
     */
    public ServerResponse getOrderCartProduct(Integer userId){

        List<Cart> cartList = cartMapper.selectByUserWhenChecked(userId);
       ServerResponse serverResponse = this.getOrderItemList(userId, cartList);
       if (!serverResponse.isSuccess())
           return serverResponse;

       List<OrderItemVo> orderItemVoList = Lists.newArrayList();
       List<OrderItem> orderItemList = (List<OrderItem>)serverResponse.getData();
       BigDecimal bigDecimal = new BigDecimal(0);

       for (OrderItem orderItem : orderItemList){
           OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
           orderItemVoList.add(orderItemVo);
           bigDecimal = BigDecimalUtil.add(bigDecimal.doubleValue(), orderItem.getTotalPrice().doubleValue());
       }

       OrderProductVo orderProductVo = new OrderProductVo();
       orderProductVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
       orderProductVo.setOrderItemVoList(orderItemVoList);
       orderProductVo.setProductTotalPrice(bigDecimal);

       return ServerResponse.CreateBySuccess(orderProductVo);
    }

    public ServerResponse createOrder(Integer userId, Integer shippingId){

        //添加购物车已经被复选的商品到List
        List<Cart> cartList = cartMapper.selectByUserWhenChecked(userId);

        //todo:从购物车中获取数据
        ServerResponse serverResponse = getOrderItemList(userId, cartList);

        if (!serverResponse.isSuccess())
            return serverResponse;

        // //计算这个订单的总价
        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        ////生成订单
        Order order = generatedOrder(userId, shippingId, payment);
        if (null == order)
            return ServerResponse.CreateByErrorMessage("生成订单失败");
        if (CollectionUtils.isEmpty(orderItemList))
            return ServerResponse.CreateByErrorMessage("购物车为空");

        for(OrderItem orderItem : orderItemList)
            orderItem.setOrderNo(order.getOrderNo());

        //mybatis 批量插入
        orderItemMapper.batchInsert(orderItemList);

        //生成成功,我们要减少我们产品的库存
        this.reduceProductStock(orderItemList);

          //清空一下购物车
        this.clearCart(cartList);

        // //返回给前端数据
        return ServerResponse.CreateBySuccess(assembleOrderVo(order, orderItemList));

    }

    public ServerResponse getAllList(Integer pageSize, Integer pageNum){

        PageHelper.startPage(pageSize, pageNum);
        List<Order> orderList = orderMapper.selectOrder();

        List<OrderVo> orderVoList = assembleOrderVoList(orderList, null);
        PageInfo result = new PageInfo(orderList);
        result.setList(orderVoList);

        return ServerResponse.CreateBySuccess(result);
    }

    public ServerResponse searchOrder(Long orderNo, Integer pageSize, Integer pageNum){

        if (null == orderNo)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        PageHelper.startPage(pageSize, pageNum);
        Order order = orderMapper.selectOrderByorderNo(orderNo);
        if (null == order)
            return ServerResponse.CreateByErrorMessage("订单不存在");

        List<OrderItem> orderItemList = orderItemMapper.selectByorderNo(orderNo);
        OrderVo orderVo = assembleOrderVo(order, orderItemList);

        PageInfo pageInfo = new PageInfo(Lists.newArrayList(order));
        pageInfo.setList(Lists.newArrayList(orderVo));

        return ServerResponse.CreateBySuccess(pageInfo);
    }

    public ServerResponse orderDetail(Long orderNo){

        if (null == orderNo)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Order order = orderMapper.selectOrderByorderNo(orderNo);
        if (null == order)
            return ServerResponse.CreateByErrorMessage("订单不存在");

        List<OrderItem> orderItemList = orderItemMapper.selectByorderNo(orderNo);
        OrderVo orderVo = assembleOrderVo(order, orderItemList);

        return ServerResponse.CreateBySuccess(orderVo);
    }

    public ServerResponse sendGoods(Long orderNo){

        if (null == orderNo)
            return ServerResponse.CreateByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());

        Order order = orderMapper.selectOrderByorderNo(orderNo);

        if (null != order){
            if (Constant.OrderStatusEnum.PAID.getCode() == order.getStatus()){
               order = null;
               order.setStatus(Constant.OrderStatusEnum.SHIPPED.getCode());
               order.setSendTime(new Date());
               int resultCount = orderMapper.updateByPrimaryKeySelective(order);
               if (0 < resultCount)
                   return ServerResponse.CreateBySuccessMessage("发货成功");
               return  ServerResponse.CreateByErrorMessage("发货失败");
            }
            return ServerResponse.CreateByErrorMessage("订单状态有误");
        }

        return ServerResponse.CreateByErrorMessage("订单不存在");
    }

    private void clearCart(List<Cart> cartList){

        for (Cart cart : cartList)
            cartMapper.deleteByPrimaryKey(cart.getId());
    }

    private void reduceProductStock(List<OrderItem> orderItemList){

        for (OrderItem orderItem : orderItemList) {
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            product.setStock(product.getStock() - orderItem.getQuantity());
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    private Order generatedOrder(Integer userId, Integer shippingId, BigDecimal payment){

        Order order = new Order();

        order.setStatus(Constant.OrderStatusEnum.NO_PAY.getCode());
        order.setOrderNo(this.generateOrderNo());
        order.setPayment(payment);
        order.setShippingId(shippingId);
        order.setUserId(userId);
        order.setPostage(0);

        int resultCount = orderMapper.insert(order);
        if (0 < resultCount)
            return order;

        return null;
    }

    private ServerResponse getOrderItemList(Integer userId, List<Cart> cartList){

        if(CollectionUtils.isEmpty(cartList))
            return ServerResponse.CreateByError("购物车为空");

        List<OrderItem> orderItemList = Lists.newArrayList();

        //检查产品的状态以及数量
        for (Cart cart : cartList){
            Integer quantity = cart.getQuantity();
            Integer productId = cart.getProductId();
            Product product = productMapper.selectByPrimaryKey(productId);
            if (null == product)
                return ServerResponse.CreateByErrorMessage("产品不存在");
            if (product.getStatus() != Constant.ProductStatusEnum.ON_SALE.getCode())
                return ServerResponse.CreateByErrorMessage("产品：" + product.getName() + "不在在线售卖状态");
            if (product.getStatus() < cart.getQuantity())
                return ServerResponse.CreateByErrorMessage("产品：" + product.getName() + "库存不足");

            OrderItem orderItem = new OrderItem();
            orderItem.setCurrentUnitPrice(product.getPrice());
          //  orderItem.setOrderNo(generateOrderNo());
            orderItem.setProductId(productId);
            orderItem.setProductImage(product.getMainImage());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(quantity);
            orderItem.setTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), quantity));
            orderItem.setUserId(userId);


            orderItemList.add(orderItem);
        }

        return ServerResponse.CreateBySuccess(orderItemList);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList){

        BigDecimal bigDecimal = new BigDecimal(0);

        for (OrderItem orderItem : orderItemList){
            bigDecimal = BigDecimalUtil.add(bigDecimal.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }

        return  bigDecimal;
    }

    private Long generateOrderNo(){
        Long currentSystemDate = System.currentTimeMillis();

        return currentSystemDate + new Random().nextInt(100);
    }

    private  List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){

        List<OrderVo> orderVoList = Lists.newArrayList();

        for (Order order: orderList){
            List<OrderItem> orderItemList = Lists.newArrayList();

            //todo:管理员查询的时候，不需要传用户ID
            if (null == userId){
                orderItemList = orderItemMapper.selectByorderNo(order.getOrderNo());
            }else {
                orderItemList = orderItemMapper.selectByorderNoWithUserId(order.getOrderNo(), userId);
            }
           OrderVo orderVo = assembleOrderVo(order, orderItemList);
            orderVoList.add(orderVo);
        }

        return orderVoList;
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList){

        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for (OrderItem orderItem: orderItemList){
            OrderItemVo orderItemVo = assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }

        OrderVo orderVo = new OrderVo();

        orderVo.setCloseTime(order.getCloseTime());
        orderVo.setCreateTime(order.getCreateTime());
        orderVo.setSendTime(order.getSendTime());
        orderVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix"));
        orderVo.setOrderItemVoList(orderItemVoList);
        orderVo.setPayment(order.getPayment());
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPaymentTime(order.getPaymentTime());
        orderVo.setPaymentType(order.getPaymentType());
        String paymentDesc = Constant.PaymentTypeEnum.valueOf(order.getPaymentType()).getValue();
        orderVo.setPaymentTypeDesc(paymentDesc);
        orderVo.setPostage(order.getPostage());
        orderVo.setShippingId(order.getShippingId());

        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if (null != shipping)
        {
            orderVo.setShippingVo(assembleShippingVo(shipping));
            orderVo.setReceiveName(shipping.getReceiverName());
        }
        orderVo.setStatus(order.getStatus());
        String statusDesc = Constant.OrderStatusEnum.valueOf(order.getStatus()).getValue();
        orderVo.setStatusDesc(statusDesc);

        return orderVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping){

        ShippingVo shippingVo = new ShippingVo();

        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverProvince(shipping.getReceiverProvince());
        shippingVo.setReceiverZip(shipping.getReceiverZip());

        return shippingVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem){

        OrderItemVo orderItemVo = new OrderItemVo();

        orderItemVo.setCreateTime(orderItem.getCreateTime());
        orderItemVo.setCurrentUnitPrice(orderItem.getCurrentUnitPrice());
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductImage(orderItem.getProductImage());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setQuantity(orderItem.getQuantity());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());

        return orderItemVo;
    }
}


