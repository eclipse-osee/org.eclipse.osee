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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.word.WordAnnotationHandler;

/**
 * @author Roberto E. Escobar
 */
public class WordAttributeTrackChangeHealthOperation extends DatabaseHealthOperation {

   public WordAttributeTrackChangeHealthOperation() {
      super("Word Attribute Track Change Enabled");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#getFixTaskName()
    */
   @Override
   public String getFixTaskName() {
      return Strings.emptyString();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.dbHealth.DatabaseHealthOperation#doHealthCheck(org.eclipse.core.runtime.IProgressMonitor)
    */
   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      List<AttrData> attributesWithErrors = new ArrayList<AttrData>();

      IOperation operation =
            new FindAllTrackChangeWordAttributes("Find all Word Attributes with track changes enabled",
                  getStatus().getPlugin(), attributesWithErrors);
      doSubWork(operation, monitor, 0.90);

      setItemsToFix(attributesWithErrors.size());

      if (isShowDetailsEnabled()) {
         appendToDetails(AHTML.beginMultiColumnTable(100, 1));
         appendToDetails(AHTML.beginMultiColumnTable(100, 1));
         appendToDetails(AHTML.addHeaderRowMultiColumnTable(new String[] {"HRID", "GAMMA ID", "URI"}));
         for (AttrData attrData : attributesWithErrors) {
            appendToDetails(AHTML.addRowMultiColumnTable(new String[] {attrData.getHrid(), attrData.getGammaId(),
                  attrData.getUri()}));
         }
         appendToDetails(AHTML.endMultiColumnTable());
         monitor.worked(calculateWork(0.10));
      }
      getSummary().append(String.format("[%s] Word Attributes with Track Changes Enabled", attributesWithErrors.size()));

      monitor.worked(calculateWork(0.10));
   }

   private final static class FindAllTrackChangeWordAttributes extends AbstractOperation {
      private static final String GET_ATTRS =
            "SELECT * FROM osee_attribute t1, osee_artifact t3 WHERE t1.attr_type_id = ? AND t1.art_id = t3.art_id AND t1.uri is not null";
      private final List<AttrData> attributesWithErrors;

      public FindAllTrackChangeWordAttributes(String operationName, String pluginId, List<AttrData> attributesWithErrors) {
         super(operationName, pluginId);
         this.attributesWithErrors = attributesWithErrors;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.core.operation.AbstractOperation#doWork(org.eclipse.core.runtime.IProgressMonitor)
       */
      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         String[] attributeTypeNames =
               new String[] {WordAttribute.WORD_TEMPLATE_CONTENT, WordAttribute.WHOLE_WORD_CONTENT};

         monitor.subTask("Load Attribute Data");
         List<AttrData> attrDatas = new ArrayList<AttrData>();
         for (String attributeTypeName : attributeTypeNames) {
            AttributeType attributeType = AttributeTypeManager.getType(attributeTypeName);
            attrDatas.addAll(loadAttributeData(monitor, attributeType));
         }
         monitor.worked(calculateWork(0.20));

         if (!attrDatas.isEmpty()) {
            int totalAttrs = attrDatas.size();
            int work = calculateWork(0.80) / totalAttrs;
            for (int index = 0; index < attrDatas.size(); index++) {
               checkForCancelledStatus(monitor);
               AttrData attrData = attrDatas.get(index);
               monitor.subTask(String.format("[%s of %s] - hrid[%s]", index, totalAttrs, attrData.getHrid()));
               Resource resource = ResourceUtil.getResource(attrData.getUri());
               if (WordAnnotationHandler.containsWordAnnotations(resource.data)) {
                  // Collect Info or Try to fix not sure what to do yet
                  attributesWithErrors.add(attrData);
               }
               monitor.worked(work);
            }
         } else {
            monitor.worked(calculateWork(0.80));
         }
      }

      private List<AttrData> loadAttributeData(IProgressMonitor monitor, AttributeType attributeType) throws OseeDataStoreException, OseeTypeDoesNotExist {
         List<AttrData> attrData = new ArrayList<AttrData>();
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(GET_ATTRS, attributeType.getAttrTypeId());
            while (chStmt.next()) {
               checkForCancelledStatus(monitor);
               attrData.add(new AttrData(chStmt.getString("gamma_Id"), chStmt.getString("human_readable_id"),
                     chStmt.getString("uri")));
            }
         } finally {
            chStmt.close();
         }
         return attrData;
      }
   }

   private final static class ResourceUtil {
      private ResourceUtil() {
      }

      public static Resource getResource(String resourcePath) throws OseeCoreException {
         Resource toReturn = null;
         ByteArrayOutputStream sourceOutputStream = new ByteArrayOutputStream();
         try {
            Map<String, String> parameterMap = new HashMap<String, String>();
            parameterMap.put("sessionId", ClientSessionManager.getSessionId());
            parameterMap.put("uri", resourcePath);
            String urlString =
                  HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT,
                        parameterMap);

            AcquireResult result = HttpProcessor.acquire(new URL(urlString), sourceOutputStream);
            if (result.getCode() == HttpURLConnection.HTTP_OK) {
               toReturn = new Resource(resourcePath, result, sourceOutputStream.toByteArray());
            }
         } catch (Exception ex) {
            throw new OseeWrappedException(ex);
         } finally {
            try {
               sourceOutputStream.close();
            } catch (IOException ex) {
               throw new OseeWrappedException(ex);
            }
         }
         return toReturn;
      }

      public static void uploadResource(String gammaId, Resource resource) throws Exception {
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
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT, parameterMap);
         HttpProcessor.put(new URL(urlString), new ByteArrayInputStream(toUpload), resource.result.getContentType(),
               resource.result.getEncoding());
      }

      public static void backupResourceLocally(File backupFolder, Resource resource) throws IOException {
         InputStream inputStream = null;
         OutputStream outputStream = null;
         try {
            String path = resource.sourcePath;
            path = path.replaceAll("attr://", "");
            path = path.replaceAll("/", Lib.isWindows() ? "\\\\" : "/");
            File file = new File(backupFolder, path);
            File parent = file.getParentFile();
            if (parent != null) {
               parent.mkdirs();
            }
            outputStream = new FileOutputStream(file);

            inputStream = new ByteArrayInputStream(resource.rawBytes);
            Lib.inputStreamToOutputStream(inputStream, outputStream);
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
            if (outputStream != null) {
               outputStream.close();
            }
         }
      }
   }

   private final static class Resource {
      private final String entryName;
      private final String resourceName;
      private final AcquireResult result;
      private final byte[] rawBytes;
      private final boolean wasZipped;
      private final String sourcePath;

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
            this.entryName = Lib.decompressStream(new ByteArrayInputStream(rawBytes), outputStream);
            this.encoding = "UTF-8";
            this.data = new String(outputStream.toByteArray(), encoding);
         } else {
            this.data = new String(rawBytes, result.getEncoding());
            this.entryName = null;
            this.encoding = result.getEncoding();
         }
      }
   }

   private final static class AttrData {
      private final String gammaId;
      private final String hrid;
      private final String uri;

      public AttrData(String gammaId, String hrid, String uri) {
         super();
         this.gammaId = gammaId;
         this.hrid = hrid;
         this.uri = uri;
      }

      public String getGammaId() {
         return gammaId;
      }

      public String getHrid() {
         return hrid;
      }

      public String getUri() {
         return uri;
      }
   }
}
