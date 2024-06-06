package filterclass;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;


@WebFilter("/api/*")
public class CorsFilter implements Filter {
       
    
    public CorsFilter() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	public void destroy() {
		// TODO Auto-generated method stub
	}

	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		   HttpServletResponse resp = (HttpServletResponse)response;
	        resp.setHeader("Access-Control-Allow-Origin", "*");
	        resp.setHeader("Access-Control-Allow-Headers", "*");
	        resp.setHeader("Access-Control-Allow-Methods", "*");
		
		chain.doFilter(request, response);
	}

	
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
