package person.mmall.commom;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import java.io.Serializable;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json对象，如果是null对象，key也会消失
public class ServerResponse<T> implements Serializable {

    private int status;
    private String msg;
    private T data;

    ServerResponse(int status){

        this.status = status;
    }

    ServerResponse(int status, String msg){

        this.status = status;
        this.msg = msg;
    }

    ServerResponse(int status, String msg, T data){

        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    ServerResponse(int status, T data){

        this.status = status;
        this.data = data;
    }

    @JsonIgnore
    public boolean isSuccess(){

        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public int getStatus(){

        return this.status;
    }

    public String getMsg(){

        return this.msg;
    }

    public T getData(){

        return this.data;
    }

    public static <T> ServerResponse<T> CreateBySuccess(){

        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> CreateBySuccessMessage(String msg){

        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(),msg);
    }

    public static <T> ServerResponse<T> CreateBySuccess(T data){

        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> CreateBySuccess(String msg, T data){

        return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public  static <T> ServerResponse<T> CreateByError(){

        return new ServerResponse<T>(ResponseCode.ERROR.getCode());
    }

    public static <T> ServerResponse<T> CreateByErrorMessage(String msg){

        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), msg);
    }

    public static <T> ServerResponse<T> CreateByError(T data){

        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), data);
    }

    public static <T> ServerResponse<T> CreateByError(String msg, T data){

        return new ServerResponse<T>(ResponseCode.ERROR.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> CreateByErrorCodeMessage(int code, String errorMsg){

        return new ServerResponse<T>(code,errorMsg);
    }

}
