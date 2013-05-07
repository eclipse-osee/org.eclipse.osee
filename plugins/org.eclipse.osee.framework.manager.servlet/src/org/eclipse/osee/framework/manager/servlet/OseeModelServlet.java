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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsTypes;

/**
 * @author Roberto E. Escobar
 */
public class OseeModelServlet extends SecureOseeHttpServlet {

   private static final long serialVersionUID = -2639113870500561780L;

   private final OrcsApi orcsApi;
   private final IDataTranslationService dataTransalatorService;

   public OseeModelServlet(Log logger, ISessionManager sessionManager, IDataTranslationService dataTransalatorService, OrcsApi orcsApi) {
      super(logger, sessionManager);
      this.dataTransalatorService = dataTransalatorService;
      this.orcsApi = orcsApi;
   }

   private OrcsTypes getOrcsTypes() {
      return orcsApi.getOrcsTypes(null);
   }

   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      if (!request.getMethod().equalsIgnoreCase("GET")) {
         super.checkAccessControl(request);
      }
   }

   private void handleError(HttpServletResponse resp, String request, Throwable th) throws IOException {
      getLogger().error(th, "Osee Cache request error: [%s]", request);
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      resp.setContentType("text/plain");
      resp.getWriter().write(Lib.exceptionToString(th));
      resp.getWriter().flush();
      resp.getWriter().close();
   }

   @Override
   protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      try {
         ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

         getOrcsTypes().exportOseeTypes(outputStream);
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.setCharacterEncoding("UTF-8");

         Lib.inputStreamToOutputStream(new ByteArrayInputStream(outputStream.toByteArray()), resp.getOutputStream());
      } catch (Exception ex) {
         handleError(resp, req.getQueryString(), ex);
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      try {
         IDataTranslationService service = dataTransalatorService;
         OseeImportModelRequest modelRequest =
            service.convert(req.getInputStream(), CoreTranslatorId.OSEE_IMPORT_MODEL_REQUEST);

         OseeImportModelResponse modelResponse = new OseeImportModelResponse();

         getOrcsTypes().importOseeTypes(isInitializing(req), modelRequest, modelResponse);

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
