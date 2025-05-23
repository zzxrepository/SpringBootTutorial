package com.zzx.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        long start = System.currentTimeMillis();
        chain.doFilter(req, res);
        System.out.println(request.getMethod() + " " + request.getRequestURI() +
            " took " + (System.currentTimeMillis() - start) + "ms");
    }
}