/*
 * Created on May 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.artifact.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.common.io.Streams;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFileServlet extends HttpServlet {

   private static final long serialVersionUID = -6334080268467740905L;

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      InputStream inputStream = null;
      boolean wasProcessed = false;
      try {
         HttpArtifactFileInfo artifactFileInfo = new HttpArtifactFileInfo(request);
         String uri = null;
         if (artifactFileInfo.isBranchNameValid()) {
            uri = ArtifactUtil.getUri(artifactFileInfo.getGuid(), artifactFileInfo.getBranchName());
         } else {
            uri = ArtifactUtil.getUri(artifactFileInfo.getGuid(), artifactFileInfo.getBranchId());
         }
         if (Strings.isValid(uri)) {
            IResourceLocator locator = Activator.getInstance().getResourceLocatorManager().getResourceLocator(uri);
            Options options = new Options();
            options.put(StandardOptions.DecompressOnAquire.name(), true);
            IResource resource = Activator.getInstance().getResourceManager().acquire(locator, options);

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
               response.setHeader("Content-Disposition", "attachment; filename=" + resource.getName());

               Streams.inputStreamToOutputStream(inputStream, response.getOutputStream());
            }
         }

         if (!wasProcessed) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
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
      OseeLog.log(Activator.class.getName(), Level.SEVERE, message, ex);
      response.getWriter().write(Lib.exceptionToString(ex));
   }
}
