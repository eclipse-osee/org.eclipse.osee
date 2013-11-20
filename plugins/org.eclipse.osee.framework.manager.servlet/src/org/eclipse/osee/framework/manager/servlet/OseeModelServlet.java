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
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.OseeImportModelRequest;
import org.eclipse.osee.framework.core.model.OseeImportModelResponse;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.resource.management.IResource;
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
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      try {
         getOrcsTypes().writeTypes(output).call();
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.setCharacterEncoding("UTF-8");

         Lib.inputStreamToOutputStream(new ByteArrayInputStream(output.toByteArray()), resp.getOutputStream());
      } catch (Exception ex) {
         handleError(resp, req.getQueryString(), ex);
      }
   }

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      try {
         final OseeImportModelRequest modelRequest =
            dataTransalatorService.convert(req.getInputStream(), CoreTranslatorId.OSEE_IMPORT_MODEL_REQUEST);

         IResource resource = new IResource() {

            @Override
            public InputStream getContent() throws OseeCoreException {
               InputStream inputStream = null;
               try {
                  inputStream = new ByteArrayInputStream(modelRequest.getModel().getBytes("UTF-8"));
               } catch (UnsupportedEncodingException ex) {
                  OseeExceptions.wrapAndThrow(ex);
               }
               return inputStream;
            }

            @Override
            public URI getLocation() {
               try {
                  String modelName = modelRequest.getModelName();
                  if (!modelName.endsWith(".osee")) {
                     modelName += ".osee";
                  }
                  return new URI("osee:/" + modelName);
               } catch (URISyntaxException ex) {
                  getLogger().error(ex, "Error creating location URI for model import");
               }
               return null;
            }

            @Override
            public String getName() {
               return modelRequest.getModelName();
            }

            @Override
            public boolean isCompressed() {
               return false;
            }

         };

         OseeImportModelResponse modelResponse = new OseeImportModelResponse();

         getOrcsTypes().loadTypes(resource, isInitializing(req)).call();

         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/xml");
         resp.setCharacterEncoding("UTF-8");

         InputStream inputStream =
            dataTransalatorService.convertToStream(modelResponse, CoreTranslatorId.OSEE_IMPORT_MODEL_RESPONSE);
         Lib.inputStreamToOutputStream(inputStream, resp.getOutputStream());
      } catch (Exception ex) {
         handleError(resp, req.toString(), ex);
      }
   }
}
