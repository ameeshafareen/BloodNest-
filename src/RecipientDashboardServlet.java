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

public class RecipientDashboardServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"RECIPIENT".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        int userId = (Integer) session.getAttribute("userId");
        String recipientName = "";
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT CONCAT(first_name, ' ', last_name) as name FROM recipients WHERE user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) recipientName = rs.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("<!DOCTYPE html><html><head><title>Recipient Dashboard</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Recipient Panel</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/recipient/dashboard' class='active'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/recipient/request'>Request Blood</a>");
        out.println("<a href='" + request.getContextPath() + "/recipient/status'>Request Status</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><h1>Welcome, " + recipientName + "</h1>");
        out.println("<div class='info-box'><h2>Blood Inventory</h2>");
        out.println("<table class='data-table'><thead><tr><th>Blood Group</th><th>Units Available</th></tr></thead><tbody>");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT bg.group_name, bi.units_available FROM blood_inventory bi " +
                "JOIN blood_groups bg ON bi.blood_group_id = bg.blood_group_id ORDER BY bg.group_name");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                out.println("<tr><td>" + rs.getString("group_name") + "</td><td>" + rs.getInt("units_available") + "</td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("</tbody></table></div></div></body></html>");
    }
}
