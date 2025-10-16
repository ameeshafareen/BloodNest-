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

public class RecipientStatusServlet extends HttpServlet {
    
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
        
        out.println("<!DOCTYPE html><html><head><title>Request Status</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Recipient Panel</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/recipient/dashboard'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/recipient/request'>Request Blood</a>");
        out.println("<a href='" + request.getContextPath() + "/recipient/status' class='active'>Request Status</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><h1>My Blood Requests</h1>");
        
        if (request.getParameter("success") != null) {
            out.println("<div class='success-msg'>Request submitted successfully!</div>");
        }
        
        out.println("<table class='data-table'><thead><tr><th>Date</th><th>Blood Group</th><th>Units</th><th>Required Date</th><th>Status</th></tr></thead><tbody>");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT br.request_date, bg.group_name, br.units_required, br.required_date, br.status " +
                "FROM blood_requests br JOIN recipients r ON br.recipient_id = r.recipient_id " +
                "JOIN blood_groups bg ON br.blood_group_id = bg.blood_group_id " +
                "WHERE r.user_id = ? ORDER BY br.request_date DESC");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                String status = rs.getString("status");
                String statusClass = "PENDING".equals(status) ? "status-orange" : 
                                   ("APPROVED".equals(status) ? "status-green" : "status-red");
                out.println("<tr><td>" + rs.getDate("request_date") + "</td>");
                out.println("<td>" + rs.getString("group_name") + "</td>");
                out.println("<td>" + rs.getInt("units_required") + "</td>");
                out.println("<td>" + rs.getDate("required_date") + "</td>");
                out.println("<td><span class='" + statusClass + "'>" + status + "</span></td></tr>");
            }
            
            if (!hasRecords) {
                out.println("<tr><td colspan='5' style='text-align:center'>No requests found</td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("</tbody></table></div></body></html>");
    }
}
