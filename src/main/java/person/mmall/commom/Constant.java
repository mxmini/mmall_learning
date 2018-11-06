package person.mmall.commom;

import com.google.common.collect.Sets;

import java.util.Set;

public class Constant {

    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final  String CURRENT_USER = "currentUser";

    public interface Role{

        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public interface Cart{
        int CHECKED = 1;//即购物车选中状态
        int UN_CHECKED = 0;//购物车中未选中状态

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface ProductOrderBy{
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc", "price_desc");
    }

    public enum ProductStatusEnum{
        //'商品状态.1-在售 2-下架 3-删除',
        ON_SALE(1,"在线"),
        OFF_SALE(2,"下架"),
        DELETE(3, "删除");

        private int code;
        private String desc;
        ProductStatusEnum(int code,String  desc){
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    public enum OrderStatusEnum{
        //订单状态:0-已取消-10-未付款，20-已付款，40-已发货，50-订单成功，60-订单关闭
        CANCELED(0, "已取消"),
        NO_PAY(10, "未付款"),
        PAID(20, "已付款"),
        SHIPPED(40, "已发货"),
        ORDER_SUCCESS(50, "订单完成"),
        ORDER_CLOSED(60, "订单关闭");

        private int code;
        private String value;

        OrderStatusEnum(int code, String value){
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static OrderStatusEnum valueOf(int code){

            for (OrderStatusEnum orderStatusEnum: values()){
                if (code == orderStatusEnum.getCode())
                    return orderStatusEnum;
            }

            throw  new RuntimeException("么有找到对应的枚举");
        }

    }

    public enum PaymentTypeEnum{
        ONLINE_PAY(1, "在线支付");

        private int code;
        private String value;

        PaymentTypeEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public int getCode() {
            return code;
        }

        public String getValue() {
            return value;
        }

        public static PaymentTypeEnum valueOf(int code){

            for(PaymentTypeEnum paymentTypeEnum: values()){
                if (code == paymentTypeEnum.getCode()){
                    return paymentTypeEnum;
                }
            }

            throw new RuntimeException("么有找到对应的枚举");
        }

    }

}
