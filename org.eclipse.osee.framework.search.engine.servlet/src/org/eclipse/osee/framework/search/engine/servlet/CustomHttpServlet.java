/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.search.engine.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.jdk.core.util.Strings;

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
         String message = null;
         String requestMethod = request.getMethod();
         String queryString = request.getQueryString();
         if (requestMethod.equals("GET")) {
            message = String.format("Search - %s", queryString);
         } else {
            message = String.format("Tag - %s", Strings.isValid(queryString) ? queryString : "attribute xml stream");
         }
         // System.out.println(String.format("[%s] [%s] serviced in [%s] ms", requestMethod, message, elapsed));
      }
   }

}
