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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.branch.management.IOseeBranchService;
import org.eclipse.osee.framework.core.datastore.DatastoreInitOperation;
import org.eclipse.osee.framework.core.datastore.IOseeSchemaProvider;
import org.eclipse.osee.framework.core.datastore.IOseeSchemaResource;
import org.eclipse.osee.framework.core.datastore.OseeSchemaProvider;
import org.eclipse.osee.framework.core.datastore.SchemaCreationOptions;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.DatastoreInitRequest;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.LogProgressMonitor;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;

/**
 * @author Roberto E. Escobar
 */
public class ConfigurationServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = -5421308349950133041L;

   private final IDataTranslationService translationService;
   private final IOseeDatabaseService databaseService;
   private final IOseeCachingService cachingService;
   private final IOseeBranchService branchService;
   private final IApplicationServerManager appServerService;

   public ConfigurationServlet(IApplicationServerManager appServerService, IDataTranslationService translationService, IOseeDatabaseService databaseService, IOseeCachingService cachingService, IOseeBranchService branchService) {
      this.translationService = translationService;
      this.databaseService = databaseService;
      this.branchService = branchService;
      this.cachingService = cachingService;
      this.appServerService = appServerService;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String servletPath = request.getServletPath();
      String urlPath = request.getRequestURI().replace(servletPath, "");
      if (urlPath.startsWith("/datastore/schema")) {
         StringWriter writer = new StringWriter();
         IOseeSchemaProvider schemaProvider = new OseeSchemaProvider();
         for (IOseeSchemaResource resource : schemaProvider.getSchemaResources()) {
            InputStream inputStream = null;
            try {
               inputStream = new BufferedInputStream(resource.getContent());
               writer.write(Lib.inputStreamToString(inputStream));
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            } finally {
               Lib.close(inputStream);
            }
         }
         sendMessage(response, HttpURLConnection.HTTP_OK, writer.toString(), null);
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
         try {
            SchemaCreationOptions options = getInitOptions(request);
            IOseeSchemaProvider schemaProvider = new OseeSchemaProvider();
            IOperation operation =
               new DatastoreInitOperation(appServerService, databaseService, cachingService, branchService,
                  schemaProvider, options);
            Operations.executeWorkAndCheckStatus(operation, new LogProgressMonitor(), -1.0);
         } catch (Exception ex) {
            String message = String.format("Datastore Initialization: [%s]\n%s", response.toString(), ex.toString());
            sendMessage(response, HttpURLConnection.HTTP_INTERNAL_ERROR, message, ex);
         }
      } else {
         String message = String.format("[%s] not found", request.getRequestURI());
         sendMessage(response, HttpURLConnection.HTTP_NOT_FOUND, message, null);
      }
   }

   private SchemaCreationOptions getInitOptions(HttpServletRequest request) throws Exception {
      DatastoreInitRequest data =
         translationService.convert(request.getInputStream(), CoreTranslatorId.OSEE_DATASTORE_INIT_REQUEST);

      SchemaCreationOptions options =
         new SchemaCreationOptions(data.getTableDataSpace(), data.getIndexDataSpace(), data.isUseFileSpecifiedSchemas());
      return options;
   }

   private void sendMessage(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
      if (ex != null) {
         OseeLog.log(Activator.class, Level.SEVERE, message, ex);
      }
      response.setStatus(status);
      response.setContentType("text/plain");
      response.getWriter().write(message);
      response.getWriter().flush();
      response.getWriter().close();
   }
}
