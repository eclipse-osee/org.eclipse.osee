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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilderClient;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class ResourceManipulation extends AbstractBlam {
   private static final boolean DEBUG =
         Boolean.parseBoolean(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Blam"));

   //"SELECT DISTINCT uri, human_readable_id, txs.gamma_id FROM osee_txs txs, osee_attribute t1, osee_artifact t3 WHERE t1.attr_type_id = ? AND t1.art_id = t3.art_id AND t1.uri is not null AND t1.gamma_id = txs.gamma_id and txs.tx_current = 1 AND txs.gamma_id > 4211835";
   //   private static final String GET_ATTRS =
   //         "SELECT DISTINCT uri, human_readable_id, txs.gamma_id FROM osee_tx_details det, osee_branch brn, osee_txs txs, osee_attribute t1, osee_artifact t3 WHERE t1.attr_type_id = ? AND t1.art_id = t3.art_id AND t1.uri is not null AND t1.gamma_id = txs.gamma_id and txs.transaction_id = det.transaction_id AND det.branch_id = brn.branch_id AND  branch_type in (1,2) AND txs.gamma_id > 4211835";

   private static final String GET_ATTRS =
         "SELECT * FROM osee_attribute t1, osee_artifact t3 WHERE t1.attr_type_id = ? AND t1.art_id = t3.art_id AND t1.uri is not null";
   private static final String GET_ATTRS_TEST = GET_ATTRS + " AND t1.gamma_id = 4259157";

   private final Collection<String> badData = new LinkedList<String>();
   private static final String[] columnHeaders = new String[] {"Corrupted Data"};

   private static File createTempFolder() {
      File rootDirectory = OseeData.getFile("FixTemplate_" + Lib.getDateTimeString() + File.separator);
      rootDirectory.mkdirs();
      return rootDirectory;
   }

   @Override
   public String getName() {
      return "Resource Manipulation";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      File backupFolder = createTempFolder();
      OseeLog.log(SkynetGuiPlugin.class, Level.INFO, String.format("Backup Folder location: [%s]",
            backupFolder.getAbsolutePath()));

      if (true) {
         System.out.println("An admin must enable this operation");
         return;
      }
      ArrayList<AttrData> attrDatas = loadAttrData();
      monitor.beginTask("Fix word template content", attrDatas.size());
      for (AttrData attrData : attrDatas) {
         monitor.subTask(attrData.getHrid());
         Resource resource = getResource(attrData.getUri());
         byte[] originalData = resource.data;
         byte[] finalVersion = new byte[0];
         int count = 0;
         int countlast = 0;
         System.out.println("   " + attrData.getUri() + " " + attrData.getGammaId());
         List<Byte> badByteBuffer = new LinkedList<Byte>();
         int byteCount = 0;
         int initialByte = 0;
         int startByte = 0;
         for (byte byt : originalData) {
            if (byt < 0) {
               if (count == 0) {
                  initialByte = byteCount;
                  badByteBuffer.clear();
               }
               badByteBuffer.add(new Byte(byt).byteValue());
               String value = UnicodeConverter.getValue(badByteBuffer);
               if (value != null) {
                  if (value.contains("GOOD")) {
                     System.out.println(String.format("                Found the Clean Value %s", value));
                     System.out.print("                       ");
                     for (byte byter : badByteBuffer) {
                        System.out.print(byter + ", ");
                     }
                     System.out.println();
                  } else {
                     byte[] goodBytes = UnicodeConverter.getGoodBytes(value);
                     System.out.println(String.format("     Found the Value %s", value));
                     System.out.print("     ");
                     finalVersion = fixBytes(startByte, initialByte, goodBytes, finalVersion, originalData);
                     startByte = finalVersion.length + 1;
                     for (byte byter : badByteBuffer) {
                        System.out.print(byter + ", ");
                     }
                     System.out.println();
                     System.out.print("      ");
                     for (byte byter : goodBytes) {
                        System.out.print(byter + ", ");
                     }
                     System.out.println();
                     startByte = byteCount + 1;
                  }
                  count = 0;
               } else {
                  count++;
               }
            } else {
               if (count > 0) {
                  System.out.println("Couldn't Find the Value for");
                  for (byte byter : badByteBuffer) {
                     System.out.print(byter + ", ");
                  }
                  System.out.println("");
               }
               count = 0;
               badByteBuffer.clear();
            }
            byteCount++;

            //Need to add on the tail if needs be
         }

         finalVersion = addTail(startByte, finalVersion, originalData);
         count = 0;
         countlast = 0;
         System.out.println("ORIGINAL");
         for (byte byt : originalData) {
            count++;
            if (byt < 0) {
               if (countlast + 1 != count) {
                  System.out.println("");
               }
               System.out.println(count + "   " + byt + " " + new String(
                     new byte[] {originalData[count - 8], originalData[count - 7], originalData[count - 6],
                           originalData[count - 5], originalData[count - 4], originalData[count - 3],
                           originalData[count - 2], originalData[count - 1], originalData[count],
                           originalData[count + 1], originalData[count + 2], originalData[count + 3],
                           originalData[count + 4], originalData[count + 5], originalData[count + 6]}, "UTF-8"));
               countlast = count;
            }
         }

         System.out.println("FIXED");
         count = 0;
         countlast = 0;
         for (byte byt : finalVersion) {
            count++;
            if (byt < 0) {
               if (countlast + 1 != count) {
                  System.out.println("");
               }
               System.out.println(count + "   " + byt + " " + new String(new byte[] {finalVersion[count - 9],
                     finalVersion[count - 8], finalVersion[count - 7], finalVersion[count - 6],
                     finalVersion[count - 5], finalVersion[count - 4], finalVersion[count - 3],
                     finalVersion[count - 2], finalVersion[count - 1], finalVersion[count], finalVersion[count + 1],
                     finalVersion[count + 2], finalVersion[count + 3], finalVersion[count + 4],
                     finalVersion[count + 5], finalVersion[count + 6]}, "UTF-8"));
               countlast = count;
            }
         }

         try {
            // Backup File
            backupResourceLocally(backupFolder, resource);

            // Perform Fix

            resource.data = finalVersion;
            //              = new String(WordTemplateRenderer.getFormattedContent(rootElement), "UTF-8");
            //                           resource.encoding = "UTF-8";
            //

            // UploadResource
            uploadResource(attrData.getGammaId(), resource);

            if (DEBUG) {
               OseeLog.log(SkynetGuiPlugin.class, Level.INFO, String.format(" After Fix : %s", resource.data));
            }
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format("Skiping File %s because of exception %s",
                  attrData.getHrid(), ex));
         }
         monitor.worked(1);
      }

      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.beginMultiColumnTable(100, 1));
      sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
      for (String string : badData) {
         sbFull.append(AHTML.addRowMultiColumnTable(new String[] {string}));
      }
      sbFull.append(AHTML.endMultiColumnTable());
      XResultData rd = new XResultData();
      rd.addRaw(sbFull.toString());
      rd.report("Fix bad data", Manipulations.RAW_HTML);
   }

   /**
    * @param startByte
    * @param initialByte
    * @param goodBytes
    * @param finalVersion
    * @return
    */
   private byte[] fixBytes(int startByte, int initialByte, byte[] goodBytes, byte[] finalVersion, byte[] originalData) {
      byte[] fixed = new byte[initialByte - startByte + goodBytes.length + finalVersion.length];
      int count = 0;
      for (byte byt : finalVersion) {
         fixed[count] = byt;
         count++;
      }
      for (int x = startByte; x < initialByte; x++) {
         fixed[count] = originalData[x];
         count++;
      }
      for (byte byt : goodBytes) {
         fixed[count] = byt;
         count++;
      }
      return fixed;
   }

   /**
    * @param startByte
    * @param initialByte
    * @param goodBytes
    * @param finalVersion
    * @return
    */
   private byte[] addTail(int startByte, byte[] finalVersion, byte[] originalData) {
      byte[] fixed = new byte[originalData.length - startByte + finalVersion.length];
      int count = 0;
      for (byte byt : finalVersion) {
         fixed[count] = byt;
         count++;
      }
      for (int x = startByte; x < originalData.length; x++) {
         fixed[count] = originalData[x];
         count++;
      }
      return fixed;
   }

   private ArrayList<AttrData> loadAttrData() throws OseeCoreException {
      ArrayList<AttrData> attrData = new ArrayList<AttrData>();

      IOseeStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(GET_ATTRS_TEST,
               AttributeTypeManager.getType(WordAttribute.WORD_TEMPLATE_CONTENT).getId());
         while (chStmt.next()) {
            attrData.add(new AttrData(chStmt.getString("gamma_Id"), chStmt.getString("human_readable_id"),
                  chStmt.getString("uri")));
         }
      } finally {
         chStmt.close();
      }
      return attrData;
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

      byte[] toUpload = resource.data;
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
               HttpUrlBuilderClient.getInstance().getOsgiServletServiceUrl(OseeServerContext.RESOURCE_CONTEXT, parameterMap);

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

   private final class Resource {
      private final String entryName;
      private final String resourceName;
      private final AcquireResult result;
      private final byte[] rawBytes;
      private final boolean wasZipped;
      private final String sourcePath;

      private byte[] data;
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
            this.data = outputStream.toByteArray();
         } else {
            this.data = rawBytes;
            this.entryName = null;
            this.encoding = result.getEncoding();
         }
      }
   }

   private static void backupResourceLocally(File backupFolder, Resource resource) throws IOException {
      InputStream inputStream = null;
      OutputStream outputStream = null;
      try {
         String path = resource.sourcePath.replace("attr://", "");
         //path = path.replaceAll("/", File.separator);

         File file = new File(backupFolder, path);
         file.getParentFile().mkdirs();

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

   @Override
   public String getXWidgetsXml() {
      return AbstractBlam.emptyXWidgetsXml;
   }

   class AttrData {
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

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }
}
