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

public class AdminDonorsServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html><html><head><title>Donors List</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Admin</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/admin/dashboard'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/inventory'>Inventory</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/requests'>Requests</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><h1>Registered Donors</h1>");
        out.println("<table class='data-table'><thead><tr><th>Name</th><th>Blood Group</th><th>Contact</th><th>City</th><th>Status</th></tr></thead><tbody>");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT CONCAT(d.first_name, ' ', d.last_name) as name, bg.group_name, d.contact_number, " +
                "d.city, d.is_active FROM donors d JOIN blood_groups bg ON d.blood_group_id = bg.blood_group_id");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                out.println("<tr><td>" + rs.getString("name") + "</td><td>" + rs.getString("group_name") + "</td>");
                out.println("<td>" + rs.getString("contact_number") + "</td><td>" + rs.getString("city") + "</td>");
                out.println("<td>" + (rs.getBoolean("is_active") ? "Active" : "Inactive") + "</td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("</tbody></table></div></body></html>");
    }
}
