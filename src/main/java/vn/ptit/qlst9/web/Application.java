package vn.ptit.qlst9.web;

import jakarta.servlet.*;
import vn.ptit.qlst9.dao.DAO;

public class Application implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) {
        try {DAO.initialize();}catch(Exception e) {throw new IllegalStateException("Không thể khởi tạo CSDL",e);}
    }
}
