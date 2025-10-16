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

public class LoginServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String error = request.getParameter("error");
        String logout = request.getParameter("logout");
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Login - Blood Bank</title>");
        out.println("<link rel='stylesheet' href='" + request.getContextPath() + "/css/style.css'>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class='container'>");
        out.println("<div class='login-box'>");
        out.println("<h2>Blood Bank Management System</h2>");
        out.println("<h3>Login</h3>");
        
        if (logout != null) {
            out.println("<div class='success-msg'>Logged out successfully!</div>");
        }
        if (error != null) {
            out.println("<div class='error-msg'>Invalid email or password!</div>");
        }
        
        out.println("<form method='post' action='" + request.getContextPath() + "/login'>");
        out.println("<div class='form-group'>");
        out.println("<label>Email:</label>");
        out.println("<input type='email' name='email' required>");
        out.println("</div>");
        out.println("<div class='form-group'>");
        out.println("<label>Password:</label>");
        out.println("<input type='password' name='password' required>");
        out.println("</div>");
        out.println("<button type='submit' class='btn-primary'>Login</button>");
        out.println("</form>");
        out.println("<div class='links'>");
        out.println("<p>New user? Register as:</p>");
        out.println("<a href='" + request.getContextPath() + "/donor/register'>Donor</a> | ");
        out.println("<a href='" + request.getContextPath() + "/recipient/register'>Recipient</a>");
        out.println("</div>");
        out.println("</div>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try (Connection conn = dbconnect.getConnection()) {
            String sql = "SELECT user_id, email, role FROM users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", rs.getInt("user_id"));
                session.setAttribute("email", rs.getString("email"));
                session.setAttribute("role", rs.getString("role"));
                
                String role = rs.getString("role");
                if ("ADMIN".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else if ("DONOR".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/donor/dashboard");
                } else if ("RECIPIENT".equals(role)) {
                    response.sendRedirect(request.getContextPath() + "/recipient/dashboard");
                }
            } else {
                response.sendRedirect(request.getContextPath() + "/login?error=1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/login?error=1");
        }
    }
}
