package bloodbank.servlet;

import bloodbank.dbconnect;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

public class DonorRegisterServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html><html><head><title>Donor Registration</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'></head><body>");
        out.println("<div class='container'><div class='form-box'><h2>Donor Registration</h2>");
        out.println("<form method='post'>");
        out.println("<div class='form-row'>");
        out.println("<div class='form-group'><label>First Name:</label><input type='text' name='firstName' required></div>");
        out.println("<div class='form-group'><label>Last Name:</label><input type='text' name='lastName' required></div>");
        out.println("</div>");
        out.println("<div class='form-group'><label>Email:</label><input type='email' name='email' required></div>");
        out.println("<div class='form-group'><label>Password:</label><input type='password' name='password' required></div>");
        out.println("<div class='form-row'>");
        out.println("<div class='form-group'><label>Gender:</label><select name='gender' required>");
        out.println("<option value='MALE'>Male</option><option value='FEMALE'>Female</option><option value='OTHER'>Other</option></select></div>");
        out.println("<div class='form-group'><label>Date of Birth:</label><input type='date' name='dob' required></div>");
        out.println("</div>");
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
        out.println("<div class='form-group'><label>Contact Number:</label><input type='text' name='contact' required></div>");
        out.println("<div class='form-group'><label>Address:</label><textarea name='address' required></textarea></div>");
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
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try (Connection conn = dbconnect.getConnection()) {
            conn.setAutoCommit(false);
            
            // Insert user
            PreparedStatement stmt1 = conn.prepareStatement("INSERT INTO users (email, password, role) VALUES (?, ?, 'DONOR')", Statement.RETURN_GENERATED_KEYS);
            stmt1.setString(1, email);
            stmt1.setString(2, password);
            stmt1.executeUpdate();
            
            ResultSet rs = stmt1.getGeneratedKeys();
            int userId = 0;
            if (rs.next()) userId = rs.getInt(1);
            
            // Insert donor
            PreparedStatement stmt2 = conn.prepareStatement(
                "INSERT INTO donors (user_id, first_name, last_name, gender, date_of_birth, blood_group_id, " +
                "contact_number, address, city, state, pincode) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            stmt2.setInt(1, userId);
            stmt2.setString(2, request.getParameter("firstName"));
            stmt2.setString(3, request.getParameter("lastName"));
            stmt2.setString(4, request.getParameter("gender"));
            stmt2.setString(5, request.getParameter("dob"));
            stmt2.setInt(6, Integer.parseInt(request.getParameter("bloodGroup")));
            stmt2.setString(7, request.getParameter("contact"));
            stmt2.setString(8, request.getParameter("address"));
            stmt2.setString(9, request.getParameter("city"));
            stmt2.setString(10, request.getParameter("state"));
            stmt2.setString(11, request.getParameter("pincode"));
            stmt2.executeUpdate();
            
            conn.commit();
            response.sendRedirect(request.getContextPath() + "/login");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/donor/register?error=1");
        }
    }
}
