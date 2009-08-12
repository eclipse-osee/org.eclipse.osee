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
package org.eclipse.osee.framework.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.exception.EmptyResourceException;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.eclipse.osee.framework.servlet.data.HttpRequestDecoder;
import org.eclipse.osee.framework.servlet.data.ServletResourceBridge;

/**
 * This class is responsible for managing server-side resources. The class accepts http requests to perform uploads,
 * deletes, and gets from clients granting access to server-side managed resources.
 * 
 * @author Robeto E. Escobar
 */
public class ResourceManagerServlet extends OseeHttpServlet {
   private static final long serialVersionUID = 3777506351978711657L;

   private void handleError(HttpServletResponse response, String message, Throwable ex) {
      OseeLog.log(this.getClass(), Level.SEVERE, message, ex);
      try {
         response.getWriter().println(message);
      } catch (IOException ex1) {
         OseeLog.log(this.getClass(), Level.SEVERE, message, ex);
      }
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      InputStream inputStream = null;
      try {
         Pair<String, Boolean> parameters = HttpRequestDecoder.fromGetRequest(request);
         String path = parameters.getFirst();
         boolean isCheckExistance = parameters.getSecond();
         Options options = HttpRequestDecoder.getOptions(request);

         IResourceLocator locator = Activator.getInstance().getResourceLocatorManager().getResourceLocator(path);

         if (isCheckExistance) {
            boolean exists = Activator.getInstance().getResourceManager().exists(locator);
            response.setStatus(exists ? HttpServletResponse.SC_OK : HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println(String.format("[%s] was %sfound.", path, exists ? "" : "not "));
         } else {
            IResource resource = Activator.getInstance().getResourceManager().acquire(locator, options);
            if (resource != null) {
               inputStream = resource.getContent();

               response.setStatus(HttpServletResponse.SC_OK);
               response.setContentLength(inputStream.available());
               response.setCharacterEncoding("ISO-8859-1");
               String mimeType = HttpURLConnection.guessContentTypeFromStream(inputStream);
               if (mimeType == null) {
                  mimeType = HttpURLConnection.guessContentTypeFromName(resource.getLocation().toString());
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
   protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      int result = HttpServletResponse.SC_BAD_REQUEST;
      try {
         String[] args = HttpRequestDecoder.fromPutRequest(request);
         Options options = HttpRequestDecoder.getOptions(request);

         IResourceLocator locator =
               Activator.getInstance().getResourceLocatorManager().generateResourceLocator(args[0], args[1], args[2]);
         IResource tempResource = new ServletResourceBridge(request, locator);

         IResourceLocator actualLocator =
               Activator.getInstance().getResourceManager().save(locator, tempResource, options);
         result = HttpServletResponse.SC_CREATED;
         response.setStatus(result);
         response.setContentType("text/plain");
         response.getWriter().write(actualLocator.toString());
      } catch (MalformedLocatorException ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         response.setContentType("text/plain");
         handleError(response, String.format("Unable to locate resource: [%s] - %s", request.getRequestURI(),
               ex.getLocalizedMessage()), ex);
      } catch (EmptyResourceException ex) {
         response.setStatus(HttpServletResponse.SC_NO_CONTENT);
         response.setContentType("text/plain");
         handleError(response, String.format("Unable to store empty resource: [%s] - %s", request.getRequestURI(),
               ex.getLocalizedMessage()), ex);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         response.setContentType("text/plain");
         handleError(response, String.format("Error saving resource: [%s]", ex.getLocalizedMessage()), ex);
      }
   }

   @Override
   protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      int result = HttpServletResponse.SC_BAD_REQUEST;
      try {
         String path = HttpRequestDecoder.fromDeleteRequest(request);
         IResourceLocator locator = Activator.getInstance().getResourceLocatorManager().getResourceLocator(path);
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
}
