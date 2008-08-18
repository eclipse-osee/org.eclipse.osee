/*
 * Created on Aug 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.exportImport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;

/**
 * @author Roberto E. Escobar
 */
public class NewBranchExporter {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(NewBranchExporter.class);
   private static final String XML_EXTENSION = ".xml";
   private static final String ZIP_EXTENSION = ".zip";

   private static final String ATTRIBUTE_TABLE_QUERY =
         "select attr1.art_id, attr1.attr_id, attr1.attr_type_id, attr1.modification_id, attr1.gamma_id, attr1.value, attr1.uri, atyp1.name from osee_define_attribute attr1, osee_define_attribute_type atyp1, osee_define_txs txs1, osee_define_tx_details txd1 where txs1.transaction_id = txd1.transaction_id and txs1.gamma_id = attr1.gamma_id and atyp1.attr_type_id = attr1.attr_type_id and txd1.branch_id = 1 ORDER BY attr1.attr_id";

   private static List<BaseRelationalItem> relationalItems = null;

   private boolean includeBaselineTxs;
   private File targetFile;

   private NewBranchExporter(File targetFile, boolean includeBaselineTxs) {
      this.includeBaselineTxs = includeBaselineTxs;
      this.targetFile = targetFile;

      if (relationalItems == null) {
         relationalItems = new ArrayList<BaseRelationalItem>();
         relationalItems.add(new AttributeRelationItem());
      }
   }

   private void export(final IProgressMonitor monitor) throws Exception {
      long start = System.currentTimeMillis();
      File tempFolder = createTempFolder(targetFile);

      int queryId = -1; //TODO Put Branch Ids in Join;

      for (BaseRelationalItem relationalItem : relationalItems) {
         relationalItem.export(tempFolder, queryId);
      }

      String zipTargetName = tempFolder.getName() + ZIP_EXTENSION;
      String message = String.format("Compressing Branch Export Data - [%s]", zipTargetName);
      monitor.subTask(message);
      logger.log(Level.INFO, message);
      File zipTarget = new File(targetFile.getParent(), zipTargetName);
      Lib.compressDirectory(tempFolder, zipTarget.getAbsolutePath(), true);
      Lib.deleteDir(tempFolder);
      logger.log(Level.INFO, String.format("Exported [%s] branches in [%s ms]", "", Lib.getElapseString(start)));
   }

   private File createTempFolder(File targetFile) {
      String baseName = Lib.removeExtension(targetFile.getName());
      File rootDirectory = new File(targetFile.getParentFile(), baseName);
      rootDirectory.mkdirs();
      return rootDirectory;
   }

   public static void export(IProgressMonitor monitor, File targetFile, boolean includeBaselineTxs, int... branchId) throws Exception {
      NewBranchExporter branchExporter = new NewBranchExporter(targetFile, includeBaselineTxs);
      branchExporter.export(monitor);
   }

   private abstract class BaseRelationalItem {
      private String name;
      private String query;

      public BaseRelationalItem(String name, String query) {
         this.name = name;
         this.query = query;
      }

      public String getName() {
         return name;
      }

      public String getQuery() {
         return query;
      }

      public int getFileSizeEstimate() {
         return (int) Math.pow(2, 24);
      }

      public void export(File tempFolder, int queryId) throws Exception {
         File indexFile = new File(tempFolder, getName() + XML_EXTENSION);
         Writer writer = null;
         ConnectionHandlerStatement stmt = null;
         try {
            writer =
                  new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile), "UTF-8"),
                        getFileSizeEstimate());
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");
            writer.write("<Table>\n");

            stmt = ConnectionHandler.runPreparedQuery(getQuery(), SQL3DataType.INTEGER, queryId);
            while (stmt.next()) {
               processData(tempFolder, writer, stmt.getRset());
            }

            writer.write("</Table>\n");
         } finally {
            DbUtil.close(stmt);
            if (writer != null) {
               writer.close();
            }
         }
      }

      protected abstract void processData(File tempFolder, Writer writer, ResultSet resultSet) throws Exception;
   }

   private final class AttributeRelationItem extends BaseRelationalItem {

      public AttributeRelationItem() {
         super("osee.define.attribute", ATTRIBUTE_TABLE_QUERY);
      }

      private String writeBinaryDataTo(File tempFolder, String uriTarget) throws Exception {
         String toReturn = null;
         FileOutputStream outputStream = null;
         try {
            int index = uriTarget.lastIndexOf("/");
            String fileName = uriTarget.substring(index + 1, uriTarget.length());
            File target = new File(tempFolder, fileName);
            outputStream = new FileOutputStream(target);
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("uri", uriTarget);
            String url = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("resource", parameters);
            AcquireResult acquireResult = HttpProcessor.acquire(new URL(url), outputStream);
            if (acquireResult.wasSuccessful()) {
               toReturn = target.getName();
            } else {
               throw new Exception(String.format("Error acquiring data for [%s]", uriTarget));
            }
         } finally {
            if (outputStream != null) {
               outputStream.close();
            }
         }
         return toReturn;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.exportImport.NewBranchExporter.BaseRelationalItem#processData(java.io.File, java.io.Writer, java.sql.ResultSet)
       */
      @Override
      protected void processData(File tempFolder, Writer writer, ResultSet rset) throws Exception {
         String attrTypeName = rset.getString("name");
         int artId = rset.getInt("art_id");
         int attrId = rset.getInt("attr_id");
         long gammaId = rset.getLong("gamma_id");

         ModificationType modType = ModificationType.getMod(rset.getInt("modification_id"));

         String stringValue = rset.getString("value");
         String uriData = rset.getString("uri");

         StringBuffer buffer = new StringBuffer("<Attribute ");
         buffer.append(String.format("type=\"%s\" artId=\"%s\" attrId=\"%s\" gammaId=\"%s\"", attrTypeName, artId,
               attrId, gammaId));
         if (modType != null) {
            buffer.append(" modType=\"");
            buffer.append(modType.name());
            buffer.append("\"");
         }
         buffer.append(" >");
         writer.write(buffer.toString());
         if (Strings.isValid(stringValue)) {
            writer.write("<StringValue>");
            Xml.writeAsCdata(writer, stringValue);
            writer.write("</StringValue>\n");
         }

         if (Strings.isValid(uriData)) {
            String relativePath = writeBinaryDataTo(tempFolder, uriData);
            writer.write("<BinaryData location=\"");
            writer.write(relativePath);
            writer.write("\" />\n");
         }
         writer.write("</Attribute>\n");
      }
   }
}
