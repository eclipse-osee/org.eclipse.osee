/*
 * Created on Apr 10, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.servlet;

import java.io.IOException;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Roberto E. Escobar
 */
public class CustomHttpServlet extends HttpServlet {
   private static final long serialVersionUID = -8895023500150352658L;

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      long start = System.currentTimeMillis();
      try {
         super.service(request, response);
      } finally {
         long elapsed = System.currentTimeMillis() - start;
         Activator.getInstance().getLogger().log(Level.INFO,
               String.format("[%s] [%s] serviced in [%s] ms", request.getMethod(), request.getQueryString(), elapsed));
      }
   }

}
