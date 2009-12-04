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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.osee.framework.core.data.OseeImportModelRequest;
import org.eclipse.osee.framework.core.data.OseeImportModelResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeDataTranslationProvider;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.services.IOseeModelingService;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelServlet extends OseeHttpServlet {

   private static final long serialVersionUID = -2639113870500561780L;
   private final IOseeDataTranslationProvider dataTransalatorProvider;
   
   public OseeModelServlet(IOseeDataTranslationProvider dataTransalatorProvider) {
      super();
      this.dataTransalatorProvider = dataTransalatorProvider;
   }

   private IOseeModelingService getModelingService() throws OseeCoreException {
      return MasterServletActivator.getInstance().getOseeModelingService();
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

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

         getModelingService().exportOseeTypes(new LogProgressMonitor(), outputStream);
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.setCharacterEncoding("UTF-8");

         Lib.inputStreamToOutputStream(new ByteArrayInputStream(outputStream.toByteArray()), resp.getOutputStream());
      } catch (Exception ex) {
         handleError(resp, req.getQueryString(), ex);
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
         IDataTranslationService service = dataTransalatorProvider.getTranslatorService();
         OseeImportModelRequest modelRequest =
               service.convert(req.getInputStream(), CoreTranslatorId.OSEE_IMPORT_MODEL_REQUEST);

         OseeImportModelResponse modelResponse = new OseeImportModelResponse();

         getModelingService().importOseeTypes(new LogProgressMonitor(), isInitializing(req), modelRequest,
               modelResponse);

         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/xml");
         resp.setCharacterEncoding("UTF-8");

         InputStream inputStream = service.convertToStream(modelResponse, CoreTranslatorId.OSEE_IMPORT_MODEL_RESPONSE);
         Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
      } catch (Exception ex) {
         handleError(resp, req.toString(), ex);
      }
   }
}
