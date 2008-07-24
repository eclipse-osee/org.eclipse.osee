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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.ISearchEngine;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineServlet extends HttpServlet {

   private static final long serialVersionUID = 3722992788943330970L;

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         HttpSearchInfo searchInfo = new HttpSearchInfo(request);
         ISearchEngine searchEngine = Activator.getInstance().getSearchEngine();
         String result = searchEngine.search(searchInfo.getQuery(), searchInfo.getBranchId(), searchInfo.getOptions());
         response.setCharacterEncoding("UTF-8");
         response.setContentType("text/plain");
         if (result != null && result.isEmpty() != true) {
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(result);
         } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
         }
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         OseeLog.log(Activator.class, Level.SEVERE, String.format(
               "Failed to respond to a search engine servlet request [%s]", request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
      response.getWriter().flush();
      response.getWriter().close();
   }

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      try {
         int queryId = Integer.parseInt(request.getParameter("queryId"));
         Activator.getInstance().getSearchTagger().tagByQueueQueryId(queryId);
         response.setStatus(HttpServletResponse.SC_OK);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Error submitting for tagging - [%s]",
               request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      }
      response.getWriter().flush();
      response.getWriter().close();
   }

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      InputStream inputStream = null;
      try {
         inputStream = request.getInputStream();
         Activator.getInstance().getSearchTagger().tagFromXmlStream(inputStream);

         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         response.setStatus(HttpServletResponse.SC_CREATED);

      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
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
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Error submitting for tagging - [%s]",
               request.toString()), ex);
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }
}
