package filter;

import utils.Config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Dmitry Tochilin
 */
public class PageFilter implements Filter {

    private FilterConfig filterConfig;

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String userName = (String) request.getSession().getAttribute("user");

        String command = request.getParameter("command");
        String uri = request.getRequestURI();

        if (userMustBeLogout(userName, command)) {
            request.setAttribute("redirect",
                    Config.getProperty(Config.LOGIN));
        } else {
            request.setAttribute("redirect", "/Controller");
        }

        if (uri.endsWith(".jsp")) {
            DispatcherType dt = request.getDispatcherType();
            if (dt == DispatcherType.FORWARD || dt == DispatcherType.INCLUDE) {
                filterChain.doFilter(request, servletResponse);
            } else {
                ((HttpServletResponse) servletResponse).sendError(404, "Direct access to JSP");
            }
        } else {
            filterChain.doFilter(request, servletResponse);
        }

    }

    private boolean userMustBeLogout(String userName, String command) {
        return (command == null || userName==null) &&
                !"login".equals(command) &&
                !"registration".equals(command) &&
                !"openRegistration".equals(command);
    }

    @Override
    public void destroy() {

    }

}
