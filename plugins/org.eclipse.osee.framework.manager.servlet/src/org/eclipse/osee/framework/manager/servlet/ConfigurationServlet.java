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
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.DatastoreInitRequest;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsMetaData;

/**
 * @author Roberto E. Escobar
 */
public class ConfigurationServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = -5421308349950133041L;

   private final IDataTranslationService translationService;
   private final OrcsApi orcsApi;

   public ConfigurationServlet(Log logger, IDataTranslationService translationService, OrcsApi orcsApi) {
      super(logger);
      this.translationService = translationService;
      this.orcsApi = orcsApi;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String servletPath = request.getServletPath();
      String urlPath = request.getRequestURI().replace(servletPath, "");
      if (urlPath.startsWith("/datastore/schema")) {
         OrcsAdmin adminOps = orcsApi.getAdminOps(null);

         Callable<OrcsMetaData> callable = adminOps.createFetchOrcsMetaData();

         try {
            OrcsMetaData result = callable.call();

            StringWriter writer = new StringWriter();
            writer.write(result.toString());
            sendMessage(response, HttpURLConnection.HTTP_OK, writer.toString(), null);

         } catch (Exception ex) {
            String message = String.format("Error Fetching OrcsInfo: [%s]\n%s", response.toString(), ex.toString());
            sendMessage(response, HttpURLConnection.HTTP_INTERNAL_ERROR, message, ex);
         }
      } else {
         String message = String.format("[%s] not found", request.getRequestURI());
         sendMessage(response, HttpURLConnection.HTTP_NOT_FOUND, message, null);
      }
   }

   @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String servletPath = request.getServletPath();
      String urlPath = request.getRequestURI().replace(servletPath, "");
      if (urlPath.startsWith("/datastore/initialize")) {
         OrcsAdmin adminOps = orcsApi.getAdminOps(null);

         Map<String, String> parameters = new HashMap<String, String>();

         try {
            DatastoreInitRequest data =
               translationService.convert(request.getInputStream(), CoreTranslatorId.OSEE_DATASTORE_INIT_REQUEST);
            parameters.put("schema.table.data.namespace", data.getTableDataSpace());
            parameters.put("schema.index.data.namespace", data.getIndexDataSpace());
            parameters.put("schema.user.file.specified.schema.names",
               Boolean.toString(data.isUseFileSpecifiedSchemas()));

            Callable<OrcsMetaData> callable = adminOps.createDatastore(parameters);
            OrcsMetaData result = callable.call();
            String message = "ok"; //result.toString();
            sendMessage(response, HttpURLConnection.HTTP_ACCEPTED, message, null);
         } catch (Exception ex) {
            String message = String.format("Datastore Initialization: [%s]\n%s", response.toString(), ex.toString());
            sendMessage(response, HttpURLConnection.HTTP_INTERNAL_ERROR, message, ex);
         }
      } else {
         String message = String.format("[%s] not found", request.getRequestURI());
         sendMessage(response, HttpURLConnection.HTTP_NOT_FOUND, message, null);
      }
   }

   private void sendMessage(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
      response.setStatus(status);
      response.setContentType("text/plain");
      response.getWriter().write(message);
      if (ex != null) {
         response.getWriter().write(Lib.exceptionToString(ex));
      }
      response.getWriter().flush();
      response.getWriter().close();
   }
}
