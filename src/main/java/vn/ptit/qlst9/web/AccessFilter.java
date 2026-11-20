package vn.ptit.qlst9.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.Set;
import vn.ptit.qlst9.model.WarehouseStaff9;

public class AccessFilter implements Filter {
    private static final Set<String> PUBLIC = Set.of("/", "/gdHome9.jsp", "/gdRegister9.jsp", "/doRegister9.jsp", "/gdLogin9.jsp");
    @Override public void doFilter(ServletRequest request,ServletResponse response,FilterChain chain) throws IOException,ServletException {
        var r=(HttpServletRequest)request;var out=(HttpServletResponse)response;
        r.setCharacterEncoding("UTF-8");out.setCharacterEncoding("UTF-8");
        out.setHeader("X-Content-Type-Options","nosniff");out.setHeader("X-Frame-Options","DENY");
        out.setHeader("Cache-Control","no-store");
        String path=r.getServletPath();
        if(path.startsWith("/assets/")) {chain.doFilter(r,out);return;}
        var session=r.getSession();Web.csrf(session);
        if(!PUBLIC.contains(path) && !(session.getAttribute("member") instanceof WarehouseStaff9)) {
            out.sendRedirect(r.getContextPath()+"/gdLogin9.jsp");return;
        }
        if(Web.post(r) && !session.getAttribute("csrf").equals(r.getParameter("csrf"))) {out.sendError(403);return;}
        if(path.startsWith("/do") && !Web.post(r)) {out.sendError(405);return;}
        chain.doFilter(r,out);
    }
}
