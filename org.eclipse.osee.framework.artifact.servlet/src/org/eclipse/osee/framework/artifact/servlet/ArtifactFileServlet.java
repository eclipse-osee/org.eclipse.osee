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
package org.eclipse.osee.framework.artifact.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ObjectPair;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFileServlet extends OseeHttpServlet {

   private static final long serialVersionUID = -6334080268467740905L;

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      InputStream inputStream = null;
      boolean wasProcessed = false;
      try {
         HttpArtifactFileInfo artifactFileInfo = null;

         String servletPath = request.getServletPath();
         //         System.out.println("servletPath: " + servletPath);
         if (!Strings.isValid(servletPath) || "/".equals(servletPath) || "/index".equals(servletPath)) {
            //            Enumeration<?> enumeration = request.getHeaderNames();
            //            while (enumeration.hasMoreElements()) {
            //               String headerField = (String) enumeration.nextElement();
            //               String value = request.getHeader(headerField);
            //               System.out.println(String.format("%s: %s", headerField, value));
            //            }

            ObjectPair<String, String> defaultArtifact = DefaultOseeArtifact.get();
            if (defaultArtifact != null) {
               artifactFileInfo = new HttpArtifactFileInfo(defaultArtifact.object1, null, defaultArtifact.object2);
            }
         } else {
            artifactFileInfo = new HttpArtifactFileInfo(request);
         }

         String uri = null;
         if (artifactFileInfo != null) {
            if (artifactFileInfo.isBranchNameValid()) {
               uri = ArtifactUtil.getUri(artifactFileInfo.getGuid(), artifactFileInfo.getBranchName());
            } else {
               uri = ArtifactUtil.getUri(artifactFileInfo.getGuid(), artifactFileInfo.getBranchId());
            }
         }
         if (Strings.isValid(uri)) {
            IResourceLocator locator = Activator.getResourceLocatorManager().getResourceLocator(uri);
            Options options = new Options();
            options.put(StandardOptions.DecompressOnAquire.name(), true);
            IResource resource = Activator.getResourceManager().acquire(locator, options);

            if (resource != null) {
               wasProcessed = true;
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
               if (!mimeType.equals("text/html")) {
                  response.setHeader("Content-Disposition", "attachment; filename=" + resource.getName());
               }
               Lib.inputStreamToOutputStream(inputStream, response.getOutputStream());
            }
         }

         if (!wasProcessed) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.setContentType("text/plain");
            response.getWriter().write(String.format("Unable to find resource: [%s]", request.getQueryString()));
         }
      } catch (NumberFormatException ex) {
         handleError(response, HttpServletResponse.SC_BAD_REQUEST, String.format("Invalid Branch Id: [%s]",
               request.getQueryString()), ex);
      } catch (Exception ex) {
         handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format(
               "Unable to acquire resource: [%s]", request.getQueryString()), ex);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
         response.flushBuffer();
      }
   }

   private void handleError(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
      response.setStatus(status);
      response.setContentType("text/plain");
      OseeLog.log(Activator.class, Level.SEVERE, message, ex);
      response.getWriter().write(Lib.exceptionToString(ex));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.core.server.internal.InternalOseeHttpServlet#checkAccessControl()
    */
   @Override
   protected void checkAccessControl(HttpServletRequest request) throws OseeCoreException {
      // Open to all
   }

}
