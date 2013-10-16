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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.w3c.dom.Element;

/**
 * Looking for bad sequence:
 * 
 * <pre>
 * 0xEF 0xBF 0xBD
 * </pre>
 * 
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class FixTemplateContentArtifacts extends AbstractBlam {

   //@formatter:off
	private static final String GET_TRANS_DETAILS =
			"SELECT attr.gamma_id, attr.uri, tx_d.author, tx_d.branch_id, tx_d.transaction_id, tx_d.time, tx_d.osee_comment " +
			"FROM osee_txs txs, osee_tx_details tx_d, osee_attribute attr, osee_branch branch " +
			"WHERE txs.tx_current = ? " +
			"AND tx_d.tx_type = ? " +
			"AND tx_d.transaction_id = txs.transaction_id " +
			"AND tx_d.branch_id = txs.branch_id  " +
			"AND attr.attr_type_id = ? " +
			"AND txs.gamma_id = attr.gamma_id " +
			"AND branch.branch_id = txs.branch_id ";

//	private static final String AND_ALL_BRANCHES = " AND txs.branch_id = tx_d.branch_id ";
	private static final String AND_SPECIFIC_BRANCHES = " AND txs.branch_id = ?";
	//@formatter:on

   private int branchId;

   public FixTemplateContentArtifacts() {
      super(null,
         "If branch not selected, this will scan through all NOT archived instances of WordTemplateContent attribute",
         BlamUiSource.FILE);
      branchId = -1;
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      IOseeStatement chStmt = ServiceUtil.getOseeDatabaseService().getStatement();
      try {
         monitor.setTaskName("Performing query");

         boolean allBranches = branchId == -1;

         StringBuilder query = new StringBuilder();
         query.append(GET_TRANS_DETAILS);
         query.append(allBranches ? Strings.EMPTY_STRING : AND_SPECIFIC_BRANCHES);

         Object[] params = new Object[allBranches ? 3 : 4];
         params[0] = TxChange.CURRENT.getValue();
         params[1] = TransactionDetailsType.NonBaselined.getId();
         params[2] = ServiceUtil.getIdentityService().getLocalId(CoreAttributeTypes.WordTemplateContent);
         if (!allBranches) {
            params[3] = branchId;
         }

         chStmt.runPreparedQuery(query.toString(), params);

         monitor.setTaskName("Processing results");
         int count = 0;
         while (chStmt.next() && !monitor.isCanceled()) {
            if (!Strings.isValid(chStmt.getString("uri"))) {
               continue;
            }
            AttrData attrData = new AttrData(chStmt.getString("gamma_id"), chStmt.getString("uri"));
            Resource resource = getResource(attrData.uri);
            if (++count % 2000 == 0) {
               log("processed " + count + " rows");
            }
            try {
               boolean foundOffenderByteSeq = false;
               for (int byteIndex = 0; byteIndex < resource.backingBytes.length; byteIndex++) {

                  //problem - dont be short or at the end
                  foundOffenderByteSeq = //
                     (resource.backingBytes[byteIndex] == (byte) 0xEF) && //
                     (resource.backingBytes[byteIndex + 1] == (byte) 0xBF) && //
                     (resource.backingBytes[byteIndex + 2] == (byte) 0xBD);

                  if (foundOffenderByteSeq) {

                     String comment =
                        String.format("author: %s, time: %s, osee_comment: %s, branch_id: %s, transaction_id %s",
                           chStmt.getString("author"), chStmt.getString("time"), chStmt.getString("osee_comment"),
                           chStmt.getString("branch_id"), chStmt.getString("transaction_id"));

                     String offender =
                        new String(new byte[] {
                           resource.backingBytes[byteIndex],
                           resource.backingBytes[byteIndex + 1],
                           resource.backingBytes[byteIndex + 2]}, "UTF-8");
                     log("\nOffender: " + offender);
                     log("Found a hit: " + attrData.uri);

                     int beginIndex = resource.data.indexOf(offender);
                     int maxIndex =
                        beginIndex + 10 < resource.data.length() ? beginIndex + 10 : resource.data.length() - 1;
                     beginIndex = beginIndex < 10 ? 0 : beginIndex - 10;

                     log("Reduced Chunk:\n " + resource.data.substring(beginIndex, maxIndex));

                     log("Introducing transaction:");
                     log(comment + "\n");
                     break;
                  }
               }
            } catch (Exception ex) {
               OseeLog.logf(Activator.class, Level.SEVERE, "Skiping File %s, %s because of exception %s",
                  attrData.gammaId, attrData.uri, ex);
            }
         }
         log("processed " + count + " rows");
      } finally {
         chStmt.close();
      }
   }

   @Override
   public void widgetCreated(final XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer widgetRenderer, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      String widgetLabel = xWidget.getLabel();
      if ("Specific branch".equals(widgetLabel)) {
         xWidget.addXModifiedListener(new XModifiedListener() {
            @Override
            public void widgetModified(XWidget widget) {
               Object data = xWidget.getData();
               if (data instanceof IOseeBranch) {
                  Branch branch = (Branch) data;
                  branchId = branch.getId();
               }
            }
         });
      }
   }

   private static void uploadResource(String gammaId, Resource resource) throws Exception {
      String fileName = resource.resourceName;
      Map<String, String> parameterMap = new HashMap<String, String>();
      parameterMap.put("sessionId", ClientSessionManager.getSessionId());
      parameterMap.put("is.overwrite.allowed", String.valueOf(true));
      parameterMap.put("protocol", "attr");
      parameterMap.put("seed", gammaId);

      String extension = Lib.getExtension(fileName);
      if (Strings.isValid(extension)) {
         parameterMap.put("extension", extension);
         int charToRemove = extension.length() + 1;
         fileName = fileName.substring(0, fileName.length() - charToRemove);
      }
      parameterMap.put("name", fileName);

      byte[] toUpload = resource.data.getBytes(resource.encoding);
      if (resource.wasZipped) {
         toUpload = Lib.compressStream(new ByteArrayInputStream(toUpload), resource.entryName);
      }

      String urlString =
         HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT, parameterMap);
      HttpProcessor.put(new URL(urlString), new ByteArrayInputStream(toUpload), resource.result.getContentType(),
         resource.result.getEncoding());
   }

   private Resource getResource(String resourcePath) throws OseeCoreException {
      Resource toReturn = null;
      ByteArrayOutputStream sourceOutputStream = new ByteArrayOutputStream();
      try {
         Map<String, String> parameterMap = new HashMap<String, String>();
         parameterMap.put("sessionId", ClientSessionManager.getSessionId());
         parameterMap.put("uri", resourcePath);
         String urlString =
            HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT,
               parameterMap);

         AcquireResult result = HttpProcessor.acquire(new URL(urlString), sourceOutputStream);
         if (result.getCode() == HttpURLConnection.HTTP_OK) {
            toReturn = new Resource(resourcePath, result, sourceOutputStream.toByteArray());
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         try {
            sourceOutputStream.close();
         } catch (IOException ex) {
            OseeExceptions.wrapAndThrow(ex);
         }
      }
      return toReturn;
   }

   private final class Resource {
      private final String entryName;
      private final String resourceName;
      private final AcquireResult result;
      private final byte[] rawBytes;
      private final boolean wasZipped;
      private final String sourcePath;

      public byte[] backingBytes;
      private String data;
      private String encoding;

      private Resource(String sourcePath, AcquireResult result, byte[] rawBytes) throws IOException {
         this.rawBytes = rawBytes;
         this.result = result;
         int index = sourcePath.lastIndexOf('/');
         this.sourcePath = sourcePath;
         this.resourceName = sourcePath.substring(index + 1, sourcePath.length());
         this.wasZipped = result.getContentType().contains("zip");
         if (wasZipped) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            this.entryName = decompressStream(new ByteArrayInputStream(rawBytes), outputStream);
            this.encoding = "UTF-8";
            this.backingBytes = outputStream.toByteArray();
            this.data = new String(backingBytes, encoding);
         } else {
            this.backingBytes = rawBytes;
            this.data = new String(rawBytes, result.getEncoding());
            this.entryName = null;
            this.encoding = result.getEncoding();
         }
      }
   }

   private static String decompressStream(InputStream inputStream, OutputStream outputStream) throws IOException {
      String zipEntryName = null;
      ZipInputStream zipInputStream = null;
      try {
         zipInputStream = new ZipInputStream(inputStream);
         ZipEntry entry = zipInputStream.getNextEntry();
         zipEntryName = entry.getName();
         // Transfer bytes from the ZIP file to the output file
         byte[] buf = new byte[1024];
         int len;
         while ((len = zipInputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
         }
      } finally {
         if (zipInputStream != null) {
            zipInputStream.close();
         }
      }
      return zipEntryName;
   }

   //To handle the case of sub-sections
   private boolean isBadParagraph(Element paragraph) {
      boolean badParagraph = false;
      String content = paragraph.getTextContent();
      if (content != null && content.contains("LISTNUM \"listreset\"")) {
         badParagraph = true;
      }

      return badParagraph;
   }

   class AttrData {
      private final String gammaId;
      private final String uri;
      private String comment;

      public AttrData(String gammaId, String uri) {
         this.gammaId = gammaId;
         this.uri = uri;
      }

      public String getGammaId() {
         return gammaId;
      }

      public String getUri() {
         return uri;
      }

      private void setComment(String comment) {
         this.comment = comment;
      }
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin.Health");
   }
}
