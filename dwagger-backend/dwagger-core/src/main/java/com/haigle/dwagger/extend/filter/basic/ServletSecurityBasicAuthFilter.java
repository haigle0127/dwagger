package com.haigle.dwagger.extend.filter.basic;

import com.haigle.dwagger.core.conf.GlobalConstants;
import com.haigle.dwagger.extend.util.FilterUtils;
import lombok.Data;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author haigle
 * @since dwagger
 */
@Data
public class ServletSecurityBasicAuthFilter extends AbstractSecurityFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.initServletConfig(filterConfig.getInitParameterNames(),
                filterConfig.getInitParameter("enableBasicAuth"),
                filterConfig.getInitParameter("userName"),
                filterConfig.getInitParameter("password"));
    }
    
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String url = request.getRequestURI();
        if (this.isEnableBasicAuth() && this.match(url)) {
            Object sessionObject = request.getSession().getAttribute(GlobalConstants.DWAGGER_BASIC_AUTH_SESSION);
            String auth = request.getHeader(GlobalConstants.AUTH_HEADER_NAME);
            if (this.tryCommonBasic(url, sessionObject, auth)) {
                if (sessionObject == null) {
                    request.getSession().setAttribute(GlobalConstants.DWAGGER_BASIC_AUTH_SESSION, getUserName());
                }
                chain.doFilter(servletRequest, servletResponse);
            } else {
                FilterUtils.writeForbiddenCode(response);
            }
        } else {
            chain.doFilter(servletRequest, servletResponse);
        }
    }
    
    @Override
    public void destroy() {
        this.urlFilters = null;
    }
}
