package com.tssoftgroup.tmobile.component.engine;


/**
 * HttpAbstractUtil
 * 
 * @author Tommi Laukkanen (tlaukkanen at gmail dot com)
 */
public abstract class HttpAbstractUtil {

    protected static String cookie;
    protected static String username;
    protected static String password;
    
    public static String getCookie() {
        return cookie;
    }
    
    public static void setCookie(String value) {
        cookie = value;
    }
    
    public static void setBasicAuthentication(String username, String password) {
        HttpAbstractUtil.username = username;
        HttpAbstractUtil.password = password;
    }
    
    /** Creates a new instance of HttpAbstractUtil */
    public HttpAbstractUtil() {
    }

}
