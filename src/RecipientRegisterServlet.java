package bloodbank.servlet;

import bloodbank.dbconnect;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class RecipientRegisterServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html><html><head><title>Recipient Registration</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='container'><div class='form-box'><h2>Recipient Registration</h2>");
        out.println("<form method='post'>");
        out.println("<div class='form-row'>");
        out.println("<div class='form-group'><label>First Name:</label><input type='text' name='firstName' required></div>");
        out.println("<div class='form-group'><label>Last Name:</label><input type='text' name='lastName' required></div>");
        out.println("</div>");
        out.println("<div class='form-group'><label>Email:</label><input type='email' name='email' required></div>");
        out.println("<div class='form-group'><label>Password:</label><input type='password' name='password' required></div>");
        out.println("<div class='form-group'><label>Contact Number:</label><input type='text' name='contact' required></div>");
        out.println("<div class='form-group'><label>Hospital Name:</label><input type='text' name='hospitalName' required></div>");
        out.println("<div class='form-group'><label>Hospital Address:</label><textarea name='hospitalAddress' required></textarea></div>");
        out.println("<div class='form-row'>");
        out.println("<div class='form-group'><label>City:</label><input type='text' name='city' required></div>");
        out.println("<div class='form-group'><label>State:</label><input type='text' name='state' required></div>");
        out.println("<div class='form-group'><label>Pincode:</label><input type='text' name='pincode' required></div>");
        out.println("</div>");
        out.println("<button type='submit' class='btn-primary'>Register</button>");
        out.println("</form>");
        out.println("<div class='links'><p>Already registered? <a href='" + request.getContextPath() + "/login'>Login here</a></p></div>");
        out.println("</div></div></body></html>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try (Connection conn = dbconnect.getConnection()) {
            conn.setAutoCommit(false);
            
            PreparedStatement stmt1 = conn.prepareStatement(
                "INSERT INTO users (email, password, role) VALUES (?, ?, 'RECIPIENT')", Statement.RETURN_GENERATED_KEYS);
            stmt1.setString(1, request.getParameter("email"));
            stmt1.setString(2, request.getParameter("password"));
            stmt1.executeUpdate();
            
            ResultSet rs = stmt1.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) userId = rs.getInt(1);
            
            PreparedStatement stmt2 = conn.prepareStatement(
                "INSERT INTO recipients (user_id, first_name, last_name, contact_number, hospital_name, " +
                "hospital_address, city, state, pincode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt2.setInt(1, userId);
            stmt2.setString(2, request.getParameter("firstName"));
            stmt2.setString(3, request.getParameter("lastName"));
            stmt2.setString(4, request.getParameter("contact"));
            stmt2.setString(5, request.getParameter("hospitalName"));
            stmt2.setString(6, request.getParameter("hospitalAddress"));
            stmt2.setString(7, request.getParameter("city"));
            stmt2.setString(8, request.getParameter("state"));
            stmt2.setString(9, request.getParameter("pincode"));
            stmt2.executeUpdate();
            
            conn.commit();
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/recipient/register?error=1");
        }
    }
}
