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
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.SecureOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.manager.servlet.data.HttpRequestDecoder;
import org.eclipse.osee.framework.manager.servlet.data.ServletResourceBridge;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.eclipse.osee.logger.Log;

/**
 * This class is responsible for managing server-side resources. The class accepts HTTP requests to perform uploads,
 * deletes, and gets from clients granting access to server-side managed resources.
 * 
 * @author Roberto E. Escobar
 */
public class ResourceManagerServlet extends SecureOseeHttpServlet {
   private static final long serialVersionUID = 3777506351978711657L;

   private final IResourceManager resourceManager;

   public ResourceManagerServlet(Log logger, ISessionManager sessionManager, IResourceManager resourceManager) {
      super(logger, sessionManager);
      this.resourceManager = resourceManager;
   }

   private void handleError(HttpServletResponse response, String message, Throwable ex) {
      getLogger().error(ex, message);
      try {
         response.getWriter().println(message);
      } catch (IOException ex1) {
         getLogger().error(ex, message);
      }
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      InputStream inputStream = null;
      try {
         Pair<String, Boolean> parameters = HttpRequestDecoder.fromGetRequest(request);
         String path = parameters.getFirst();
         boolean isCheckExistance = parameters.getSecond();
         PropertyStore options = HttpRequestDecoder.getOptions(request);

         IResourceLocator locator = resourceManager.getResourceLocator(path);

         if (isCheckExistance) {
            boolean exists = resourceManager.exists(locator);
            response.setStatus(exists ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(String.format("[%s] was %sfound.", path, exists ? "" : "not "));
         } else {
            IResource resource = resourceManager.acquire(locator, options);
            if (resource != null) {
               inputStream = resource.getContent();

               response.setStatus(HttpServletResponse.SC_OK);
               response.setContentLength(inputStream.available());
               response.setCharacterEncoding("ISO-8859-1");
               String mimeType = URLConnection.guessContentTypeFromStream(inputStream);
               if (mimeType == null) {
                  mimeType = URLConnection.guessContentTypeFromName(resource.getLocation().toString());
                  if (mimeType == null) {
                     mimeType = "application/*";
                  }
               }
               response.setContentType(mimeType);
               response.setHeader("Content-Disposition", "attachment; filename=" + resource.getName());

               Lib.inputStreamToOutputStream(inputStream, response.getOutputStream());
            } else {
               response.setStatus(HttpServletResponse.SC_NOT_FOUND);
               response.flushBuffer();
            }
         }
      } catch (MalformedLocatorException ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         handleError(response, String.format("Unable to locate resource: [%s]", request.getRequestURI()), ex);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.setContentType("text/plain");
         response.setCharacterEncoding("UTF-8");
         handleError(response, String.format("Unable to acquire resource: [%s]", request.getRequestURI()), ex);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   @Override
   protected void doPut(HttpServletRequest request, HttpServletResponse response) {
      int result = HttpServletResponse.SC_BAD_REQUEST;
      try {
         String[] args = HttpRequestDecoder.fromPutRequest(request);
         PropertyStore options = HttpRequestDecoder.getOptions(request);

         IResourceLocator locator = resourceManager.generateResourceLocator(args[0], args[1], args[2]);
         IResource tempResource = new ServletResourceBridge(request, locator);

         IResourceLocator actualLocator = resourceManager.save(locator, tempResource, options);
         result = HttpServletResponse.SC_CREATED;
         response.setStatus(result);
         response.setContentType("text/plain");
         response.getWriter().write(actualLocator.toString());
      } catch (MalformedLocatorException ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         handleError(response, String.format("Unable to locate resource: [%s] - %s", request.getRequestURI(), ex), ex);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.setContentType("text/plain");
         handleError(response, String.format("Error saving resource: [%s]", ex), ex);
      }
   }

   @Override
   protected void doDelete(HttpServletRequest request, HttpServletResponse response) {
      int result = HttpServletResponse.SC_BAD_REQUEST;
      try {
         String path = HttpRequestDecoder.fromDeleteRequest(request);
         IResourceLocator locator = resourceManager.getResourceLocator(path);
         int status = IResourceManager.OK;
         //Activator.getInstance().getResourceManager().delete(locator);
         if (status == IResourceManager.OK) {
            result = HttpServletResponse.SC_ACCEPTED;
         } else {
            result = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
         }
         response.setStatus(result);
         response.setContentType("text/plain");
         response.getWriter().write("Deleted: " + locator.toString());
         response.flushBuffer();
      } catch (MalformedLocatorException ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         handleError(response, String.format("Unable to locate resource: [%s]", request.getRequestURI()), ex);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.setContentType("text/plain");
         handleError(response, String.format("Unable to delete resource: [%s]", request.getRequestURI()), ex);
      }
   }

   // TODO Allow for bulk loading of resources
   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      super.doPost(req, resp); // Remove this line once implemented

      Set<IResourceLocator> locators = new HashSet<IResourceLocator>();
      for (IResourceLocator locator : locators) {
         try {
            PropertyStore options = new PropertyStore();
            resourceManager.acquire(locator, options);
         } catch (OseeCoreException ex) {
            //
         }
      }
   }
}
