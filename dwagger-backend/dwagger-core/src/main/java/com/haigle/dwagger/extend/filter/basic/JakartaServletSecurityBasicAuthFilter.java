package com.haigle.dwagger.extend.filter.basic;

import com.haigle.dwagger.core.conf.GlobalConstants;
import com.haigle.dwagger.extend.util.FilterUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;

import java.io.IOException;

/**
 * @author haigle
 * @since dwagger
 */
@Data
public class JakartaServletSecurityBasicAuthFilter extends AbstractSecurityFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.initServletConfig(filterConfig.getInitParameterNames(),
                filterConfig.getInitParameter("enableBasicAuth"),
                filterConfig.getInitParameter("userName"),
                filterConfig.getInitParameter("password"));
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
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
                filterChain.doFilter(servletRequest, servletResponse);
            } else {
                FilterUtils.writeJakartaForbiddenCode(response);
            }
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
    
    @Override
    public void destroy() {
        this.urlFilters = null;
    }
}
