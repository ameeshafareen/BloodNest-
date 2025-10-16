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

public class DonorHistoryServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"DONOR".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        int userId = (Integer) session.getAttribute("userId");
        
        out.println("<!DOCTYPE html><html><head><title>Donation History</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Donor Panel</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/donor/dashboard'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/donor/history' class='active'>Donation History</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><h1>Donation History</h1>");
        out.println("<table class='data-table'><thead><tr><th>Date</th><th>Blood Group</th><th>Units</th><th>Status</th></tr></thead><tbody>");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT don.donation_date, bg.group_name, don.units_donated, don.status " +
                "FROM donations don JOIN donors d ON don.donor_id = d.donor_id " +
                "JOIN blood_groups bg ON don.blood_group_id = bg.blood_group_id " +
                "WHERE d.user_id = ? ORDER BY don.donation_date DESC");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            boolean hasRecords = false;
            while (rs.next()) {
                hasRecords = true;
                out.println("<tr><td>" + rs.getDate("donation_date") + "</td>");
                out.println("<td>" + rs.getString("group_name") + "</td>");
                out.println("<td>" + rs.getDouble("units_donated") + "</td>");
                out.println("<td>" + rs.getString("status") + "</td></tr>");
            }
            
            if (!hasRecords) {
                out.println("<tr><td colspan='4' style='text-align:center'>No donation history found</td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("</tbody></table></div></body></html>");
    }
}
