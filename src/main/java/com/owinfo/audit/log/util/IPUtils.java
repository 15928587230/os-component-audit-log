package com.owinfo.audit.log.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class IPUtils {

    /**
     * 获取请求主机IP地址
     *
     * @return
     */
    public static String getIpAddr() {
        return getIpAddr(getRequest());
    }

    /**
     * 获取请求URL
     *
     * @return
     */
    public static String getRequestUrl() {
        HttpServletRequest request = getRequest();
        if (request == null) {
            return "unknown";
        }
        return (request.getRequestURL().toString());
    }

    /**
     * 获取请求主机IP地址,如果通过代理进来，则透过防火墙获取真实IP地址;
     * <p>
     * x-real-ip:171.111
     * x-forwarded-for:171.1.157.11
     * remote-host:171.111.157.11
     * 使用Nginx等反向代理软件， 则不能通过request.getRemoteAddr()获取IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是一串IP地址，X-Forwarded-For中第一个非unknown的有效IP字符串，则为真实IP地址
     */
    public static String getIpAddr(HttpServletRequest request) {

        if (request == null) {
            return "unknown";
        }
        String ip = null;
        try {
            ip = request.getHeader("x-real-ip");


            if (isUnknownIp(ip)) {
                ip = request.getHeader("x-forwarded-for");

            }
            if (isUnknownIp(ip)) {
                ip = request.getHeader("remote-host");
            }
            if (isUnknownIp(ip)) {
                ip = request.getHeader("Proxy-Client-IP");

            }
            if (isUnknownIp(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (isUnknownIp(ip)) {
                ip = request.getRemoteAddr();
            }
        } catch (Exception e) {
            log.error("IPUtils ERROR ", e);
        }

        //使用代理，则获取第一个IP地址
        if (!StringUtils.isEmpty(ip) && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }

        return ip;
    }


    private static boolean isUnknownIp(String ip) {
        return StringUtils.isEmpty(ip) || "unknown".equalsIgnoreCase(ip);
    }

    public static HttpServletRequest getRequest() {

        //获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        //从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

        return request;
    }
}
