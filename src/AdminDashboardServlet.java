package bloodbank.servlet;

import bloodbank.dbconnect;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminDashboardServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        int totalDonors = 0;
        int totalRecipients = 0;
        int totalDonations = 0;
        int pendingRequests = 0;
        
        try (Connection conn = dbconnect.getConnection()) {
            // Get total donors
            PreparedStatement stmt1 = conn.prepareStatement("SELECT COUNT(*) FROM donors");
            ResultSet rs1 = stmt1.executeQuery();
            if (rs1.next()) totalDonors = rs1.getInt(1);
            
            // Get total recipients
            PreparedStatement stmt2 = conn.prepareStatement("SELECT COUNT(*) FROM recipients");
            ResultSet rs2 = stmt2.executeQuery();
            if (rs2.next()) totalRecipients = rs2.getInt(1);
            
            // Get total donations
            PreparedStatement stmt3 = conn.prepareStatement("SELECT COUNT(*) FROM donations WHERE status='COMPLETED'");
            ResultSet rs3 = stmt3.executeQuery();
            if (rs3.next()) totalDonations = rs3.getInt(1);
            
            // Get pending requests
            PreparedStatement stmt4 = conn.prepareStatement("SELECT COUNT(*) FROM blood_requests WHERE status='PENDING'");
            ResultSet rs4 = stmt4.executeQuery();
            if (rs4.next()) pendingRequests = rs4.getInt(1);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Admin Dashboard - Blood Bank</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        
        // Navigation
        out.println("<div class='navbar'>");
        out.println("<h2>Blood Bank - Admin Panel</h2>");
        out.println("<div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/admin/dashboard'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/inventory'>Inventory</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/requests'>Requests</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/donors'>Donors</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/recipients'>Recipients</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a>");
        out.println("</div>");
        out.println("</div>");
        
        out.println("<div class='container'>");
        out.println("<h1>Admin Dashboard</h1>");
        
        // Statistics Cards
        out.println("<div class='stats-grid'>");
        
        out.println("<div class='stat-card stat-blue'>");
        out.println("<h3>Total Donors</h3>");
        out.println("<p class='stat-number'>" + totalDonors + "</p>");
        out.println("</div>");
        
        out.println("<div class='stat-card stat-green'>");
        out.println("<h3>Total Recipients</h3>");
        out.println("<p class='stat-number'>" + totalRecipients + "</p>");
        out.println("</div>");
        
        out.println("<div class='stat-card stat-orange'>");
        out.println("<h3>Total Donations</h3>");
        out.println("<p class='stat-number'>" + totalDonations + "</p>");
        out.println("</div>");
        
        out.println("<div class='stat-card stat-red'>");
        out.println("<h3>Pending Requests</h3>");
        out.println("<p class='stat-number'>" + pendingRequests + "</p>");
        out.println("</div>");
        
        out.println("</div>");
        
        // Quick Actions
        out.println("<div class='quick-actions'>");
        out.println("<h2>Quick Actions</h2>");
        out.println("<a href='" + request.getContextPath() + "/admin/inventory' class='action-btn'>Manage Inventory</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/requests' class='action-btn'>Review Requests</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/donors' class='action-btn'>View Donors</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/recipients' class='action-btn'>View Recipients</a>");
        out.println("</div>");
        
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
}
