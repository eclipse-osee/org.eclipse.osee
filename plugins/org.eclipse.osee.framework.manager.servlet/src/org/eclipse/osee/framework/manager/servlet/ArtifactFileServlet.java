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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.server.UnsecuredOseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.manager.servlet.data.ArtifactUtil;
import org.eclipse.osee.framework.manager.servlet.data.DefaultOseeArtifact;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.IResourceManager;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.BranchQuery;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactFileServlet extends UnsecuredOseeHttpServlet {

   private static final long serialVersionUID = -6334080268467740905L;

   private static final String GUID_KEY = "guid";
   private static final String BRANCH_NAME_KEY = "branch";
   private static final String BRANCH_UUID_KEY = "branchUuid";
   private static final String BRANCH_GUID_KEY = "branchGuid";
   private static final String MAPPING_ART = "ABKY9QDQLSaHQBiRC7wA";

   private final IResourceManager resourceManager;
   private final OrcsApi orcs;

   public ArtifactFileServlet(Log logger, IResourceManager resourceManager, OrcsApi orcs) {
      super(logger);
      this.resourceManager = resourceManager;
      this.orcs = orcs;
   }

   @Override
   protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
      try {

         String branchName = null, artifactGuid = null;
         Long branchUuid = null;

         String servletPath = request.getServletPath();
         if (!Strings.isValid(servletPath) || "/".equals(servletPath) || "/index".equals(servletPath)) {
            Pair<String, Long> defaultArtifact = DefaultOseeArtifact.get();
            if (defaultArtifact != null) {
               artifactGuid = defaultArtifact.getFirst();
               branchUuid = defaultArtifact.getSecond();
            }
         } else {
            artifactGuid = request.getParameter(GUID_KEY);
            branchName = request.getParameter(BRANCH_NAME_KEY);
            String branchGuid = request.getParameter(BRANCH_GUID_KEY);
            if (branchGuid != null) {
               getLogger().warn("Request with branch guid instead of uuid [%s]",
                  request.getRequestURL().append('?').append(request.getQueryString()));
               branchUuid = extractBranchUuid(branchGuid);
            } else {
               branchUuid = Long.parseLong(request.getParameter(BRANCH_UUID_KEY));
            }
         }

         String uri = null;
         BranchQuery query = orcs.getQueryFactory(null).branchQuery();
         if (branchName != null) {
            query.andNameEquals(branchName);
         } else if (branchUuid != null) {
            query.andUuids(branchUuid);
         }
         BranchReadable branch = query.getResults().getExactlyOne();
         Conditions.checkNotNull(branch, "branch", "Unable to determine branch");
         uri = ArtifactUtil.getUri(artifactGuid, branch);
         handleArtifactUri(resourceManager, request.getQueryString(), uri, response);
      } catch (NumberFormatException ex) {
         handleError(response, HttpServletResponse.SC_BAD_REQUEST,
            String.format("Invalid Branch Id: [%s]", request.getQueryString()), ex);
      } catch (Exception ex) {
         handleError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
            String.format("Unable to acquire resource: [%s]", request.getQueryString()), ex);
      } finally {
         response.flushBuffer();
      }
   }

   private Long extractBranchUuid(String branchGuid) {
      Long branchUuid = null;
      ArtifactReadable mapArtifact =
         orcs.getQueryFactory(null).fromBranch(CoreBranches.COMMON).andGuid(MAPPING_ART).getResults().getExactlyOne();
      String map = mapArtifact.getSoleAttributeAsString(CoreAttributeTypes.GeneralStringData);
      int start = map.indexOf(branchGuid);
      if (start > 0) {
         int separator = map.indexOf(":", start);
         int end = map.indexOf(";", separator);
         branchUuid = Long.parseLong(map.substring(separator + 1, end));
      } else {
         getLogger().error("BranchGuid [%s] not found in lookup artifact", branchGuid);
      }
      return branchUuid;
   }

   public static void handleArtifactUri(IResourceManager resourceManager, String request, String uri, HttpServletResponse response) throws OseeCoreException {
      boolean wasProcessed = false;
      if (Strings.isValid(uri)) {
         IResourceLocator locator = resourceManager.getResourceLocator(uri);
         PropertyStore options = new PropertyStore();
         options.put(StandardOptions.DecompressOnAquire.name(), true);
         IResource resource = resourceManager.acquire(locator, options);

         if (resource != null) {
            wasProcessed = true;

            InputStream inputStream = null;
            try {
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
               if (!mimeType.equals("text/html")) {
                  response.setHeader("Content-Disposition", "attachment; filename=" + resource.getName());
               }
               Lib.inputStreamToOutputStream(inputStream, response.getOutputStream());
               response.flushBuffer();
            } catch (IOException ex) {
               OseeExceptions.wrapAndThrow(ex);
            } finally {
               Lib.close(inputStream);
            }
         }
      }
      if (!wasProcessed) {
         response.setStatus(HttpServletResponse.SC_NOT_FOUND);
         response.setContentType("text/plain");
         try {
            response.getWriter().write(String.format("Unable to find resource: [%s]", request));
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
   }

   private void handleError(HttpServletResponse response, int status, String message, Throwable ex) throws IOException {
      response.setStatus(status);
      response.setContentType("text/plain");
      getLogger().error(ex, message);
      response.getWriter().write(Lib.exceptionToString(ex));
   }

}
