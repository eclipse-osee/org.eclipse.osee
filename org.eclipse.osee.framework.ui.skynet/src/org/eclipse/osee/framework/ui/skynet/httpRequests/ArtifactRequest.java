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
package org.eclipse.osee.framework.ui.skynet.httpRequests;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.linking.HttpRequest;
import org.eclipse.osee.framework.skynet.core.linking.HttpResponse;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.skynet.artifact.snapshot.ArtifactSnapshotManager;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.Renderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactRequest implements IHttpServerRequest {
   private static final String GUID_KEY = "guid";
   private static final String BRANCH_NAME_KEY = "branch";
   private static final String BRANCH_ID_KEY = "branchId";
   private static final String TRANSACTION_NUMBER_KEY = "transaction";
   private static final String FORCE_KEY = "force";
   private static final String FORMAT_KEY = "format";
   private static final HttpUrlBuilder urlBuilder = HttpUrlBuilder.getInstance();
   private static final ArtifactRequest instance = new ArtifactRequest();
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactRequest.class);

   public enum FormatEnums {
      HTML, NATIVE
   }

   private ArtifactRequest() {
   }

   public static ArtifactRequest getInstance() {
      return instance;
   }

   private Map<String, String> getParameters(Artifact artifact) {
      Map<String, String> keyValues = new HashMap<String, String>();
      String guid = artifact.getGuid();
      int branch = artifact.getBranch().getBranchId();
      if (Strings.isValid(guid)) {
         keyValues.put(GUID_KEY, guid);
      }
      keyValues.put(BRANCH_ID_KEY, Integer.toString(branch));
      if (artifact.isHistorical()) {
         int txNumber = artifact.getTransactionNumber();
         keyValues.put(TRANSACTION_NUMBER_KEY, Integer.toString(txNumber));
      }
      // This was added to fix browser refresh problem
      // parameter is guaranteed to be different every time ensuring browser will request the page.
      keyValues.put("date", Long.toString(new Date().getTime()));
      return keyValues;
   }

   public String getUrl(Artifact artifact) {
      return urlBuilder.getUrlForLocalSkynetHttpServer(getRequestType(), getParameters(artifact));
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#getRequestType()
    */
   public String getRequestType() {
      return "GET.ARTIFACT";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.skynet.core.linking.IHttpServerRequest#processRequest(org.eclipse.osee.framework.skynet.core.linking.HttpRequest,
    *      org.eclipse.osee.framework.skynet.core.linking.HttpResponse)
    */
   public void processRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
      boolean updateCache = Boolean.parseBoolean(httpRequest.getParameter(FORCE_KEY));
      long start = System.currentTimeMillis();
      try {
         final Artifact artifact = getRequestedArtifact(httpRequest);
         final FormatEnums requesttedFormat = getFormatType(httpRequest);
         switch (requesttedFormat) {
            case NATIVE:
               sendAsNative(artifact, httpResponse);
               break;
            case HTML:
            default:
               sendAsHTML(artifact, updateCache, httpResponse);
               break;
         }
      } catch (Exception ex) {
         logger.log(Level.WARNING, String.format("Get Artifact Error: [%s]", httpRequest.getParametersAsString()), ex);
         httpResponse.outputStandardError(400, "Exception handling request", ex);
      }
      logger.log(Level.INFO, String.format("Time to serve Artifact Request: [%s] ms.",
            System.currentTimeMillis() - start));
   }

   private Artifact getRequestedArtifact(HttpRequest httpRequest) throws Exception {
      String guidKey = httpRequest.getParameter(GUID_KEY);
      String branchIdKey = httpRequest.getParameter(BRANCH_ID_KEY);
      String branchNameKey = httpRequest.getParameter(BRANCH_NAME_KEY);
      String transactionKey = httpRequest.getParameter(TRANSACTION_NUMBER_KEY);
      Artifact toReturn;

      if (Strings.isValid(transactionKey)) {
         toReturn = getArtifactBasedOnTransactionNumber(guidKey, Integer.parseInt(transactionKey));
      } else {
         toReturn = getLatestArtifactForBranch(guidKey, branchIdKey, branchNameKey);
      }
      return toReturn;
   }

   private FormatEnums getFormatType(HttpRequest httpRequest) {
      String format = httpRequest.getParameter(FORMAT_KEY);
      FormatEnums toReturn = FormatEnums.HTML;
      try {
         toReturn = FormatEnums.valueOf(format.toUpperCase());
      } catch (Exception ex) {
         toReturn = FormatEnums.HTML;
      }
      return toReturn;
   }

   private void sendAsHTML(Artifact artifact, boolean updateCache, HttpResponse httpResponse) throws Exception {
      String html = ArtifactSnapshotManager.getInstance().getDataSnapshot(artifact, updateCache);
      httpResponse.getPrintStream().println(AHTML.pageEncoding(html));
   }

   private void sendAsNative(Artifact artifact, HttpResponse httpResponse) throws Exception {
      IRenderer render = RendererManager.getInstance().getBestRenderer(PresentationType.EDIT, artifact);
      if (render instanceof FileSystemRenderer) {
         FileSystemRenderer fileSystemRenderer = (FileSystemRenderer) render;
         Branch branch = artifact.getBranch();
         IFolder baseFolder = fileSystemRenderer.getRenderFolder(branch, PresentationType.EDIT);
         IFile iFile =
               fileSystemRenderer.renderToFileSystem(new NullProgressMonitor(), baseFolder, artifact, branch, null,
                     PresentationType.EDIT);

         File file = iFile.getLocation().toFile();
         String fileName = artifact.getDescriptiveName() + "." + iFile.getFileExtension();
         String encodedFileName = URLEncoder.encode(fileName, "UTF-8");

         httpResponse.setReponseHeader("Accept-Ranges", "bytes");
         httpResponse.setContentType("application");
         httpResponse.setContentEncoding(iFile.getCharset());
         httpResponse.setContentDisposition("attachment; filename=" + encodedFileName);
         httpResponse.sendResponseHeaders(200, file.length());

         httpResponse.sendBody(new FileInputStream(file));

         iFile.delete(true, new NullProgressMonitor());
      } else if (render instanceof Renderer) {
         sendAsHTML(artifact, false, httpResponse);
      }
   }

   private Artifact getLatestArtifactForBranch(String guid, String branchId, String branchName) throws SQLException, ArtifactDoesNotExist, MultipleArtifactsExist, NumberFormatException, BranchDoesNotExist {
      BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
      final Branch branch;
      if (Strings.isValid(branchId)) {
         branch = branchManager.getBranch(Integer.parseInt(branchId));
      } else {
         branch = branchManager.getBranch(branchName);
      }
      return ArtifactQuery.getArtifactFromId(guid, branch, true);
   }

   private Artifact getArtifactBasedOnTransactionNumber(String guid, int transactioNumber) throws Exception {
      TransactionId transactionId =
            TransactionIdManager.getInstance().getPossiblyEditableTransactionId(transactioNumber);
      return ArtifactPersistenceManager.getInstance().getArtifact(guid, transactionId);
   }
}
