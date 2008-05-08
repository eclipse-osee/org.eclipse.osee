/*
 * Created on Apr 10, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.logging.Level;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.resource.common.io.Streams;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.exception.MalformedLocatorException;
import org.eclipse.osee.framework.servlet.data.HttpRequestDecoder;
import org.eclipse.osee.framework.servlet.data.ServletResourceBridge;

/**
 * This class is responsible for managing server-side resources. The class accepts http requests to perform uploads,
 * deletes, and gets from clients granting access to server-side managed resources.
 * 
 * @author Robeto E. Escobar
 */
public class ResourceManagerServlet extends CustomHttpServlet {
   private static final long serialVersionUID = 3777506351978711657L;

   private void handleError(HttpServletResponse response, String message, Throwable ex) {
      Activator.getInstance().getLogger().log(Level.SEVERE, message, ex);
      try {
         response.getWriter().println(message);
      } catch (IOException ex1) {
         Activator.getInstance().getLogger().log(Level.SEVERE, message, ex);
      }
   }

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      InputStream inputStream = null;
      try {
         String path = HttpRequestDecoder.fromGetRequest(request);
         Options options = HttpRequestDecoder.getOptions(request);

         IResourceLocator locator = Activator.getInstance().getResourceLocatorManager().getResourceLocator(path);
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

            Streams.inputStreamToOutputStream(inputStream, response.getOutputStream());
         } else {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.flushBuffer();
         }
      } catch (MalformedLocatorException ex) {
         response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
         handleError(response, String.format("Unable to locate resource: [%s]", request.getRequestURI()), ex);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         handleError(response, String.format("Unable to acquire resource: [%s]", request.getRequestURI()), ex);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
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
         handleError(response, String.format("Unable to locate resource: [%s] - %s", request.getRequestURI(),
               ex.getLocalizedMessage()), ex);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         handleError(response, String.format("Error saving resource: [%s]", ex.getLocalizedMessage()), ex);
      }
   }

   /* (non-Javadoc)
    * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   @Override
   protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      int result = HttpServletResponse.SC_BAD_REQUEST;
      try {
         String path = HttpRequestDecoder.fromDeleteRequest(request);
         IResourceLocator locator = Activator.getInstance().getResourceLocatorManager().getResourceLocator(path);
         int status = Activator.getInstance().getResourceManager().delete(locator);
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
         handleError(response, String.format("Unable to locate resource: [%s]", request.getRequestURI()), ex);
      } catch (Exception ex) {
         response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         handleError(response, String.format("Unable to delete resource: [%s]", request.getRequestURI()), ex);
      }
   }
}
