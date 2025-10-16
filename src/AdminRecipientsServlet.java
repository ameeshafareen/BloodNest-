package bloodbank.servlet;

import bloodbank.dbconnect;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class AdminRecipientsServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html><html><head><title>Recipients List</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Admin</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/admin/dashboard'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/inventory'>Inventory</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/requests'>Requests</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><h1>Registered Recipients</h1>");
        out.println("<table class='data-table'><thead><tr><th>Name</th><th>Hospital</th><th>Contact</th><th>City</th></tr></thead><tbody>");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT CONCAT(first_name, ' ', last_name) as name, hospital_name, contact_number, city FROM recipients");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                out.println("<tr><td>" + rs.getString("name") + "</td><td>" + rs.getString("hospital_name") + "</td>");
                out.println("<td>" + rs.getString("contact_number") + "</td><td>" + rs.getString("city") + "</td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("</tbody></table></div></body></html>");
    }
}
