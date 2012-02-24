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
package org.eclipse.osee.framework.manager.servlet;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.data.TagListener;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineTaggerServlet extends SecureOseeHttpServlet {
   private static final long serialVersionUID = 5104108752343302320L;

   private final OrcsApi orcsApi;

   public SearchEngineTaggerServlet(Log logger, ISessionManager sessionManager, OrcsApi orcsApi) {
      super(logger, sessionManager);
      this.orcsApi = orcsApi;
   }

   @Override
   protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
      InputStream inputStream = null;
      try {
         boolean waitForTags = Boolean.parseBoolean(request.getParameter("wait"));
         inputStream = request.getInputStream();
         if (waitForTags) {
            TagListener listener = new TagListener();

            //TODO            searchTaggerService.tagFromXmlStream(listener, inputStream);
            //            if (listener.wasProcessed() != true) {
            //               synchronized (listener) {
            //                  listener.wait();
            //               }
            //            }
         } else {
            //TODO            searchTaggerService.tagFromXmlStream(inputStream);
         }
         Lib.inputStreamToBytes(inputStream);

         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         response.setStatus(HttpServletResponse.SC_CREATED);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         getLogger().error(ex, "Error submitting for tagging - [%s]", request.toString());
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         Lib.close(inputStream);
         response.getWriter().flush();
         response.getWriter().close();
      }
   }

   @Override
   protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         String queryId = request.getParameter("queryId");
         int value = 1; //TODO         searchTaggerService.deleteTags(Integer.parseInt(queryId));
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
         getLogger().error(ex, "Error submitting for tagging - [%s]", request.toString());
         response.getWriter().write(Lib.exceptionToString(ex));
      } finally {
         response.getWriter().flush();
         response.getWriter().close();
      }
   }
}
