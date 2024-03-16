package com.creative.utils;

public class RegexUtils {
    private static final String phoneRegex = "^1[3-9]\\d{9}$";
    private static final String emailRegex =  "^[a-zA-Z0-9_]+@[a-zA-Z0-9]+\\.[a-zA-Z]{2,3}$";
    private static final String idCardRegex = "\\d{17}[0-9Xx]";
    private static final String passwordRegex = "^[a-zA-Z0-9]{8,16}$";
    private static final String usernameOrEmailRegex = "^(\\w{6,18}|[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})$";
    /**
     * 校验手机号
     * @param phone
     * @return
     */
    public static boolean phoneMatches(String phone){
        return phone.matches(phoneRegex);
    }

    /**
     * 校验电子邮箱
     * @param email
     * @return
     */
    public static boolean emailMatches(String email){
        return email.matches(emailRegex);
    }

    /**
     * 校验身份证
     * @param idCard
     * @return
     */
    public static boolean idCardMatches(String idCard){
        return idCard.matches(idCardRegex);
    }

    /**
     * 校验密码 长度为8-16位
     * @param password
     * @return
     */
    public static boolean passwordMatches(String password){
        return password.matches(passwordRegex);
    }

    /**
     * 校验用户名：可以由字母、数字或下划线组成，长度在6到18位之间
     *           或者电子邮箱
     * @param username
     * @return
     */
    public static boolean usernameMatches(String username){
      return  username.matches(usernameOrEmailRegex);
    }
}
