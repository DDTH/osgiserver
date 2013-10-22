package com.github.ddth.example.simple;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A simple servlet which returns JSON data.
 * 
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 */
public class HelloJsonServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        doJsonResponse(resp, "GET", req.getParameter("name"));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doJsonResponse(resp, "POST", req.getParameter("name"));
    }

    private void doJsonResponse(HttpServletResponse resp, String method, String name)
            throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"method\":\"" + method + "\"");
        sb.append(",");
        sb.append("\"time\":\"" + System.currentTimeMillis());
        if (name != null) {
            sb.append(",");
            sb.append("\"hello\":\"" + name + "\"");
        }
        sb.append("}");
        resp.getWriter().write(sb.toString());
    }
}
