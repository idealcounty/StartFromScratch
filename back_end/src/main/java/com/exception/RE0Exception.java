package com.exception;

public class RE0Exception extends RuntimeException {
        public RE0Exception(String message) {
            super(message);
        }
        public static RE0Exception notLogin() {
            return new RE0Exception("未登录!");
        }
        public static RE0Exception userNameAlreadyExists() {
            return new RE0Exception("用户名已被注册!");
        }
        public static RE0Exception userNotExists() {
            return new RE0Exception("用户不存在!");
        }
        public static RE0Exception userNameOrUserPasswordError() {
            return new RE0Exception("用户名或密码错误!");
        }
        public static RE0Exception archiveNameAlreadyExists() {
            return new RE0Exception("存档已存在!");
        }
        public static RE0Exception archiveNotExists() {
            return new RE0Exception("存档不存在!");
        }
        public static RE0Exception attributeValueLimitExceeded() {
            return new RE0Exception("属性值大于30点!");
        }
}
