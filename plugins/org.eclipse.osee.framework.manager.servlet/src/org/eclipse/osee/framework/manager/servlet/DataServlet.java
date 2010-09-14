/*******************************************************************************
 * Copyright (c) 2010 Boeing.
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
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeNotFoundException;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.data.ArtifactUtil;
import org.eclipse.osee.framework.manager.servlet.internal.Activator;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;

public class DataServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = -1399699606153734250L;

   private final IResourceLocatorManager locatorManager;
   private final IResourceManager resourceManager;

   public DataServlet(IResourceLocatorManager locatorManager, IResourceManager resourceManager) {
      super();
      this.locatorManager = locatorManager;
      this.resourceManager = resourceManager;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      String urlRequest = request.getRequestURI();
      try {
         handleUriRequest(locatorManager, resourceManager, urlRequest, response);
      } catch (OseeCoreException ex) {
         handleError(response, HttpURLConnection.HTTP_INTERNAL_ERROR, "", ex);
      }
   }

   private void handleError(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
      response.setContentType("text/plain");
      OseeLog.log(Activator.class, Level.SEVERE, message, ex);
      response.sendError(status, Lib.exceptionToString(ex));
   }

   public static void handleUriRequest(IResourceLocatorManager locatorManager, IResourceManager resourceManager, String urlRequest, HttpServletResponse response) throws OseeCoreException {
      UrlParser parser = new UrlParser();
      parser.parse(urlRequest);
      String branchGuid = parser.getAttribute("branch");
      String artifactGuid = parser.getAttribute("artifact");
      String uri = ArtifactUtil.getUriByGuids(branchGuid, artifactGuid);
      ArtifactFileServlet.handleArtifactUri(locatorManager, resourceManager, urlRequest, uri, response);
   }

   private static final class UrlParser {
      private final List<String> contexts;

      public UrlParser() {
         this.contexts = new ArrayList<String>();
      }

      public void parse(String urlPath) {
         contexts.clear();
         if (Strings.isValid(urlPath)) {
            String[] items = urlPath.split("/");
            for (String item : items) {
               contexts.add(item);
            }
         }
      }

      public String getAttribute(String key) throws OseeCoreException {
         Conditions.checkNotNull(key, "attribute");
         int contextCount = contexts.size();
         for (int index = 0; index < contextCount; index++) {
            String context = contexts.get(index);
            if (context.equals(key)) {
               if (index + 1 < contextCount) {
                  return contexts.get(index + 1);
               }
            }
         }
         throw new OseeNotFoundException("Unable to find [%s]", key);
      }
   }
}
