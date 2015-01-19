/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty.util;

import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author Roberto E. Escobar
 */
public class PostCountingServlet extends HttpServlet {

   private static final long serialVersionUID = -6112225579822541495L;

   private static final String MEDIA_TYPE__TEXT_PLAIN = "text/plain";
   private static final String COUNTER_KEY = "post.counter";

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         HttpSession session = request.getSession(false);
         if (session == null) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to find session");
         } else {
            Integer counter = getCounter(session);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MEDIA_TYPE__TEXT_PLAIN);
            response.getWriter().write(String.valueOf(counter));
         }
      } finally {
         response.flushBuffer();
      }
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         HttpSession session = request.getSession();

         Integer counter = getCounter(session);
         session.setAttribute(COUNTER_KEY, ++counter);

         response.setStatus(HttpServletResponse.SC_OK);
         response.setContentType(MEDIA_TYPE__TEXT_PLAIN);
         response.getWriter().write(String.valueOf(counter));
      } finally {
         response.flushBuffer();
      }
   }

   private Integer getCounter(HttpSession httpSession) {
      Integer counter = (Integer) httpSession.getAttribute(COUNTER_KEY);
      if (counter == null) {
         counter = 0;
      }
      return counter;
   }

}