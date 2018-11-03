package person.mmall.commom;

public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),
    ERROR(1,"ERROR"),
    NEED_LOGIN(10,"NEED_LOGIN"),
    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;


     ResponseCode(int code, String description){

        this.code = code;
        this.desc = description;
    }

    public int getCode(){
        return this.code;
    }
    public String getDesc(){
        return this.desc;
    }

}
