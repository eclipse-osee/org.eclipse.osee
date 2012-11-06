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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.internal.ApplicationContextFactory;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.ApplicationContext;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.IndexerCollector;
import org.eclipse.osee.orcs.search.IndexerCollectorAdapter;
import org.eclipse.osee.orcs.search.QueryIndexer;

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

   private ApplicationContext getContext(HttpServletRequest req) {
      return ApplicationContextFactory.createContext(getSessionId(req));
   }

   private QueryIndexer getQueryIndexer(HttpServletRequest request) {
      return orcsApi.getQueryIndexer(getContext(request));
   }

   @Override
   protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         boolean waitForTags = Boolean.parseBoolean(request.getParameter("wait"));
         QueryIndexer indexer = getQueryIndexer(request);
         if (waitForTags) {
            IndexerCollector collector = new IndexerCollectorAdapter() {

               int totalToProcess = 0;
               int currentCount = 0;

               @Override
               public void onIndexTaskComplete(int indexerId, long waitTime, long processingTime) {
                  currentCount++;
                  if (currentCount >= totalToProcess) {
                     synchronized (this) {
                        notify();
                     }
                  }
               }

               @Override
               public void onIndexTaskTotalToProcess(int totalQueries) {
                  totalToProcess = totalQueries;
               }

            };

            Callable<?> callable = indexer.indexXmlStream(collector, request.getInputStream());
            callable.call();
            synchronized (collector) {
               collector.wait();
            }
         } else {
            byte[] bytes = Lib.inputStreamToBytes(request.getInputStream());
            indexer.submitXmlStream(new ByteArrayInputStream(bytes));
         }
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         response.setStatus(HttpServletResponse.SC_CREATED);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         getLogger().error(ex, "Error submitting for tagging - [%s]", request.toString());
         response.getWriter().write(Lib.exceptionToString(ex));
      }
   }

   @Override
   protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {
         String queryId = request.getParameter("queryId");
         Callable<Integer> callable = getQueryIndexer(request).deleteIndexByQueryId(Integer.parseInt(queryId));
         int value = callable.call();
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
