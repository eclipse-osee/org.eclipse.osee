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
import java.io.InputStream;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineTaggerServlet extends OseeHttpServlet {
   private static final long serialVersionUID = 5104108752343302320L;

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      long start = System.currentTimeMillis();
      try {
         StringBuffer message = new StringBuffer();
         int branchId = Integer.parseInt(request.getParameter("branchId"));
         boolean waitForTags = Boolean.parseBoolean(request.getParameter("wait"));
         if (waitForTags) {
            TagListener listener = new TagListener();
            Activator.getInstance().getSearchTagger().tagByBranchId(listener, branchId);
            if (listener.wasProcessed() != true) {
               synchronized (listener) {
                  listener.wait();
               }
            }
            message.append(String.format("Processed %d queries containing %d attributes in %d ms.",
                  listener.getQueryCount(), listener.getAttributeCount(), System.currentTimeMillis() - start));
         } else {
            Activator.getInstance().getSearchTagger().tagByBranchId(branchId);
         }
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         response.setStatus(HttpServletResponse.SC_ACCEPTED);
         response.getWriter().write(message.toString());
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Error submitting for tagging - [%s]",
               request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
      response.getWriter().flush();
      response.getWriter().close();
   }

   @Override
   protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      InputStream inputStream = null;
      try {
         boolean waitForTags = Boolean.parseBoolean(request.getParameter("wait"));
         inputStream = request.getInputStream();
         if (waitForTags) {
            TagListener listener = new TagListener();
            Activator.getInstance().getSearchTagger().tagFromXmlStream(listener, inputStream);
            if (listener.wasProcessed() != true) {
               synchronized (listener) {
                  listener.wait();
               }
            }
         } else {
            Activator.getInstance().getSearchTagger().tagFromXmlStream(inputStream);
         }
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         response.setStatus(HttpServletResponse.SC_CREATED);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Error submitting for tagging - [%s]",
               request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   @Override
   protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         String queryId = request.getParameter("queryId");
         int value = Activator.getInstance().getSearchTagger().deleteTags(Integer.parseInt(queryId));
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         if (value > 0) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
         } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
         }
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Error submitting for tagging - [%s]",
               request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }
}
