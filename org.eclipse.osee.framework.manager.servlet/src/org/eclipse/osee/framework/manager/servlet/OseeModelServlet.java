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
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.data.OseeModelRequest;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelServlet extends OseeHttpServlet {

   private static final long serialVersionUID = -2639113870500561780L;

   //   @Override
   //   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
   //      IOseeCachingService caching = MasterServletActivator.getInstance().getOseeCache();
   //      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
   //      try {
   //         Pair<Object, ITranslatorId> pair = createResponse(cacheId, caching);
   //         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
   //         resp.setContentType("text/xml");
   //         resp.setCharacterEncoding("UTF-8");
   //
   //         InputStream inputStream = null;
   //         OutputStream outputStream = null;
   //         try {
   //            inputStream = service.convertToStream(pair.getFirst(), pair.getSecond());
   //            outputStream = resp.getOutputStream();
   //            Lib.inputStreamToOutputStream(inputStream, outputStream);
   //         } catch (IOException ex) {
   //            throw new OseeWrappedException(ex);
   //         }
   //      } catch (Exception ex) {
   //         OseeLog.log(getClass(), Level.SEVERE, ex);
   //      }
   //   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();

      OseeModelRequest modelRequest = null;
      InputStream inputStream = null;
      try {
         inputStream = req.getInputStream();
         modelRequest = service.convert(inputStream, CoreTranslatorId.OSEE_CACHE_UPDATE_REQUEST);
      } catch (OseeCoreException ex) {
         handleError(resp, req.toString(), ex);
      } finally {
         Lib.close(inputStream);
      }

      try {

      } catch (Exception ex) {
         handleError(resp, req.toString(), ex);
      }
   }

   private void handleError(HttpServletResponse resp, String request, Throwable th) throws IOException {
      OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format("Osee Cache request error: [%s]", request),
            th);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.setContentType("text/plain");
      resp.getWriter().write(Lib.exceptionToString(th));
      resp.getWriter().flush();
      resp.getWriter().close();
   }

   private void sendUpdates(HttpServletRequest req, HttpServletResponse resp) throws OseeCoreException {

      IOseeCachingService caching = MasterServletActivator.getInstance().getOseeCache();

      //      OutputStream outputStream = null;
      //      try {
      //         Pair<Object, ITranslatorId> pair = createResponse(updateRequest.getCacheId(), caching);
      //
      //         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
      //         resp.setContentType("text/xml");
      //         resp.setCharacterEncoding("UTF-8");
      //
      //         inputStream = service.convertToStream(pair.getFirst(), pair.getSecond());
      //         outputStream = resp.getOutputStream();
      //         Lib.inputStreamToOutputStream(inputStream, outputStream);
      //      } catch (IOException ex) {
      //         throw new OseeWrappedException(ex);
      //      }
   }

}
