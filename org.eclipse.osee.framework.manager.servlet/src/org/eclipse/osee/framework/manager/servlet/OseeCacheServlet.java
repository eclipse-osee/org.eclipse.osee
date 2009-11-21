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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.cache.IOseeCache;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeCacheServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 6693534844874109524L;

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

      try {
         CacheOperation operation = CacheOperation.fromString(req.getParameter("function"));
         switch (operation) {
            case UPDATE:
               sendUpdates(req, resp);
               break;
            case STORE:
               throw new UnsupportedOperationException();
            default:
               throw new UnsupportedOperationException();
         }
      } catch (Exception ex) {
         OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format("Branch servlet request error: [%s]",
               req.toString()), ex);
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write(Lib.exceptionToString(ex));
      }
      resp.getWriter().flush();
      resp.getWriter().close();
   }

   private void sendUpdates(HttpServletRequest req, HttpServletResponse resp) throws OseeCoreException {
      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      IOseeCachingService caching = MasterServletActivator.getInstance().getOseeCache();

      CacheUpdateRequest updateRequest = null;
      InputStream inputStream = null;
      try {
         inputStream = req.getInputStream();
         updateRequest = service.convert(inputStream, CacheUpdateRequest.class);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         Lib.close(inputStream);
      }

      IOseeCache<?> cache = caching.getCache(updateRequest.getCacheId());
      CacheUpdateResponse response = new CacheUpdateResponse(updateRequest.getCacheId(), cache.getAll());
      OutputStream outputStream = null;
      try {
         inputStream = service.convertToStream(response);
         outputStream = resp.getOutputStream();
         Lib.inputStreamToOutputStream(inputStream, outputStream);
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } finally {
         Lib.close(inputStream);
         Lib.close(outputStream);
      }
   }
}
