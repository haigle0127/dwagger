package com.haigle.dwagger.spring.filter;

import com.haigle.dwagger.extend.filter.BasicFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

/***
 * <p>
 * {@code @since } 1.9.0
 * @author haigle
 */
public class JakartaProductionSecurityFilter extends BasicFilter implements Filter {
    
    /***
     * 是否生产环境,如果是生成环境,过滤Swagger的相关资源请求
     */
    private boolean production = false;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 判断filterConfig
        Enumeration<String> enumeration = filterConfig.getInitParameterNames();
        // SpringMVC环境中,由此init方法初始化此Filter,SpringBoot环境中则不同
        if (enumeration.hasMoreElements()) {
            setProduction(Boolean.valueOf(filterConfig.getInitParameter("production")));
        }
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (production) {
            String uri = httpServletRequest.getRequestURI();
            if (!match(uri)) {
                chain.doFilter(request, response);
            } else {
                response.setContentType("text/plain;charset=UTF-8");
                PrintWriter pw = response.getWriter();
                pw.write("You do not have permission to access this page");
                pw.flush();
            }
        } else {
            chain.doFilter(request, response);
        }
    }
    
    @Override
    public void destroy() {
        
    }
    
    public JakartaProductionSecurityFilter(boolean production) {
        this.production = production;
    }
    
    public JakartaProductionSecurityFilter() {
    }
    
    public boolean isProduction() {
        return production;
    }
    
    public void setProduction(boolean production) {
        this.production = production;
    }
}
