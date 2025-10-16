public class AdminRequestsServlet {
    
}
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

public class AdminRequestsServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html><html><head><title>Blood Requests</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Admin</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/admin/dashboard'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/inventory'>Inventory</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/requests' class='active'>Requests</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><h1>Blood Requests</h1>");
        out.println("<table class='data-table'><thead><tr><th>Recipient</th><th>Blood Group</th><th>Units</th><th>Status</th><th>Action</th></tr></thead><tbody>");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT br.request_id, CONCAT(r.first_name, ' ', r.last_name) as name, bg.group_name, " +
                "br.units_required, br.status FROM blood_requests br " +
                "JOIN recipients r ON br.recipient_id = r.recipient_id " +
                "JOIN blood_groups bg ON br.blood_group_id = bg.blood_group_id ORDER BY br.request_date DESC");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                out.println("<tr><td>" + rs.getString("name") + "</td><td>" + rs.getString("group_name") + "</td>");
                out.println("<td>" + rs.getInt("units_required") + "</td><td>" + rs.getString("status") + "</td><td>");
                if ("PENDING".equals(rs.getString("status"))) {
                    out.println("<form method='post' style='display:inline'>");
                    out.println("<input type='hidden' name='requestId' value='" + rs.getInt("request_id") + "'>");
                    out.println("<button type='submit' name='action' value='APPROVED' class='btn-success'>Approve</button>");
                    out.println("<button type='submit' name='action' value='REJECTED' class='btn-danger'>Reject</button>");
                    out.println("</form>");
                } else {
                    out.println("-");
                }
                out.println("</td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        out.println("</tbody></table></div></body></html>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE blood_requests SET status = ? WHERE request_id = ?");
            stmt.setString(1, request.getParameter("action"));
            stmt.setInt(2, Integer.parseInt(request.getParameter("requestId")));
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/requests");
    }
}
