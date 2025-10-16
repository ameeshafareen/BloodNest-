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

public class DonorDashboardServlet extends HttpServlet {
    
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
        String donorName = "";
        String bloodGroup = "";
        boolean isEligible = true;
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT CONCAT(d.first_name, ' ', d.last_name) as name, bg.group_name, d.last_donation_date " +
                "FROM donors d JOIN blood_groups bg ON d.blood_group_id = bg.blood_group_id WHERE d.user_id = ?");
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                donorName = rs.getString("name");
                bloodGroup = rs.getString("group_name");
                Date lastDonation = rs.getDate("last_donation_date");
                if (lastDonation != null) {
                    long daysSince = (System.currentTimeMillis() - lastDonation.getTime()) / (1000 * 60 * 60 * 24);
                    isEligible = daysSince >= 90;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("<!DOCTYPE html><html><head><title>Donor Dashboard</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Donor Panel</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/donor/dashboard' class='active'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/donor/history'>Donation History</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><h1>Welcome, " + donorName + "</h1>");
        out.println("<div class='info-box'><h3>Your Blood Group: " + bloodGroup + "</h3>");
        out.println("<h3>Eligibility Status: " + (isEligible ? "<span class='status-green'>Eligible to Donate</span>" : "<span class='status-red'>Not Eligible (Wait 90 days)</span>") + "</h3>");
        out.println("</div>");
        out.println("<div class='info-text'><h2>Donation Guidelines</h2>");
        out.println("<ul><li>Must be at least 18 years old</li><li>Weight should be above 50 kg</li>");
        out.println("<li>Should be in good health</li><li>Wait at least 90 days between donations</li></ul></div>");
        out.println("</div></body></html>");
    }
}
