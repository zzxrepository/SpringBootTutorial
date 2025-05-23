package com.zzx.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class PerformanceFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        if (request.getHeader("Authorization") != null) {
            long start = System.currentTimeMillis();
            chain.doFilter(req, res);
            long took = System.currentTimeMillis() - start;
            System.out.println("[PERF] " + request.getRequestURI() + " took " + took + "ms");
        } else {
            chain.doFilter(req, res);
        }
    }
}