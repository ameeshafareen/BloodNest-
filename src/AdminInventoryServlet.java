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

public class AdminInventoryServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html><html><head><title>Blood Inventory</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='navbar'><h2>Blood Bank - Admin</h2><div class='nav-links'>");
        out.println("<a href='" + request.getContextPath() + "/admin/dashboard'>Dashboard</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/inventory' class='active'>Inventory</a>");
        out.println("<a href='" + request.getContextPath() + "/admin/requests'>Requests</a>");
        out.println("<a href='" + request.getContextPath() + "/logout' class='logout-btn'>Logout</a></div></div>");
        out.println("<div class='container'><h1>Blood Inventory</h1>");
        out.println("<table class='data-table'><thead><tr><th>Blood Group</th><th>Units</th><th>Action</th></tr></thead><tbody>");
        
        try (Connection conn = dbconnect.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT bi.inventory_id, bg.group_name, bi.units_available FROM blood_inventory bi " +
                "JOIN blood_groups bg ON bi.blood_group_id = bg.blood_group_id ORDER BY bg.group_name");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                out.println("<tr><td>" + rs.getString("group_name") + "</td><td>" + rs.getInt("units_available") + "</td>");
                out.println("<td><form method='post' style='display:inline'>");
                out.println("<input type='hidden' name='inventoryId' value='" + rs.getInt("inventory_id") + "'>");
                out.println("<input type='number' name='units' value='" + rs.getInt("units_available") + "' min='0' style='width:60px'>");
                out.println("<button type='submit' class='btn-small'>Update</button></form></td></tr>");
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
            PreparedStatement stmt = conn.prepareStatement("UPDATE blood_inventory SET units_available = ? WHERE inventory_id = ?");
            stmt.setInt(1, Integer.parseInt(request.getParameter("units")));
            stmt.setInt(2, Integer.parseInt(request.getParameter("inventoryId")));
            stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/inventory");
    }
}
