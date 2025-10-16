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

public class RecipientRequestServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"RECIPIENT".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html><html><head><title>Request Blood</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Recipient Panel</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/recipient/dashboard'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/recipient/request' class='active'>Request Blood</a>");
        out.println("<a href='" + request.getContextPath() + "/recipient/status'>Request Status</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><div class='form-box'><h2>Submit Blood Request</h2>");
        out.println("<form method='post'>");
        out.println("<div class='form-group'><label>Blood Group:</label><select name='bloodGroup' required>");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT blood_group_id, group_name FROM blood_groups");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                out.println("<option value='" + rs.getInt("blood_group_id") + "'>" + rs.getString("group_name") + "</option>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("</select></div>");
        out.println("<div class='form-group'><label>Units Required:</label><input type='number' name='units' min='1' required></div>");
        out.println("<div class='form-group'><label>Required Date:</label><input type='date' name='requiredDate' required></div>");
        out.println("<div class='form-group'><label>Purpose:</label><textarea name='purpose' required></textarea></div>");
        out.println("<div class='form-group'><label>Hospital Name:</label><input type='text' name='hospitalName' required></div>");
        out.println("<div class='form-group'><label>Hospital Address:</label><textarea name='hospitalAddress' required></textarea></div>");
        out.println("<div class='form-group'><label>Contact Person:</label><input type='text' name='contactPerson' required></div>");
        out.println("<div class='form-group'><label>Contact Number:</label><input type='text' name='contactNumber' required></div>");
        out.println("<button type='submit' class='btn-primary'>Submit Request</button>");
        out.println("</form></div></div></body></html>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"RECIPIENT".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        int userId = (Integer) session.getAttribute("userId");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt1 = conn.prepareStatement("SELECT recipient_id FROM recipients WHERE user_id = ?");
            stmt1.setInt(1, userId);
            ResultSet rs = stmt1.executeQuery();
            int recipientId = 0;
            if (rs.next()) recipientId = rs.getInt("recipient_id");
            
            PreparedStatement stmt2 = conn.prepareStatement(
                "INSERT INTO blood_requests (recipient_id, blood_group_id, units_required, request_date, " +
                "required_date, purpose, hospital_name, hospital_address, contact_person, contact_number) " +
                "VALUES (?, ?, ?, CURDATE(), ?, ?, ?, ?, ?, ?)");
            stmt2.setInt(1, recipientId);
            stmt2.setInt(2, Integer.parseInt(request.getParameter("bloodGroup")));
            stmt2.setInt(3, Integer.parseInt(request.getParameter("units")));
            stmt2.setString(4, request.getParameter("requiredDate"));
            stmt2.setString(5, request.getParameter("purpose"));
            stmt2.setString(6, request.getParameter("hospitalName"));
            stmt2.setString(7, request.getParameter("hospitalAddress"));
            stmt2.setString(8, request.getParameter("contactPerson"));
            stmt2.setString(9, request.getParameter("contactNumber"));
            stmt2.executeUpdate();
            
            response.sendRedirect(request.getContextPath() + "/recipient/status?success=1");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/recipient/request?error=1");
        }
    }
}
