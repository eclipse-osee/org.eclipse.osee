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
package org.eclipse.osee.framework.skynet.core.attribute;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase;

/**
 * @author Ryan D. Brooks
 */
public class FullPortableExport {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final ConfigurationPersistenceManager configurationManager =
         ConfigurationPersistenceManager.getInstance();
   private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
   private static final String LATEST_REL_LINK =
         "(SELECT rl.rel_link_id, txd.branch_id, Max(rl.gamma_id) AS last_gamma_id" + " FROM " + SkynetDatabase.RELATION_LINK_VERSION_TABLE + " rl, " + SkynetDatabase.TRANSACTIONS_TABLE + " tx, " + SkynetDatabase.TRANSACTION_DETAIL_TABLE + " txd" + " WHERE tx.transaction_id = txd.transaction_id AND tx.gamma_id = rl.gamma_id" + " GROUP BY rl.rel_link_id, txd.branch_id) rel_table";
   private static final String queryAllRelations =
         String.format(
               "SELECT type_name, (SELECT guid FROM %s WHERE a_art_id = art_id) AS guid_a, (SELECT guid FROM %s WHERE b_art_id = art_id) AS guid_b, rationale, order_value FROM %s,%s,%s WHERE %s=%s AND %s=%s ORDER BY order_value",
               ARTIFACT_TABLE.toString(), ARTIFACT_TABLE.toString(), RELATION_LINK_VERSION_TABLE.toString(),
               RELATION_LINK_TYPE_TABLE.toString(), LATEST_REL_LINK,
               RELATION_LINK_VERSION_TABLE.column("rel_link_type_id"),
               RELATION_LINK_TYPE_TABLE.column("rel_link_type_id"), "rel_table.last_gamma_id",
               RELATION_LINK_VERSION_TABLE.column("gamma_id"));
   private final ExcelXmlWriter excelWriter;
   private CharBackedInputStream charBak;
   private String[] row;
   private HashMap<String, Integer> columnIndexHash;
   public static String ATTRIBUTE_VALUE_DELIMITER = "[@]";
   public static String ATTRIBUTE_VALUE_DELIMITER_REGEX = "\\[@\\]";

   public FullPortableExport() throws IOException {
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      columnIndexHash = new HashMap<String, Integer>();
   }

   public void createRelationsSheet(Collection<Artifact> artifacts) throws IOException, SQLException {
      writeRelationsHeader();

      HashSet<IRelationLink> links = new HashSet<IRelationLink>();
      for (Artifact artifact : artifacts) {
         links.addAll(artifact.getLinkManager().getLinks());
      }

      for (IRelationLink link : links) {
         row[0] = link.getLinkDescriptor().getName();
         row[1] = link.getArtifactA().getGuid();
         row[2] = link.getArtifactB().getGuid();
         row[3] = String.valueOf(link.getAOrder());
         row[4] = String.valueOf(link.getBOrder());
         String rationale = link.getRationale();
         row[5] = rationale.equals("") ? null : rationale;

         excelWriter.writeRow(row);
      }
      excelWriter.endSheet();
   }

   public void createRelationsSheet() throws IOException, SQLException {
      writeRelationsHeader();

      ConnectionHandlerStatement chStmt = null;
      try {
         // TODO address the following exception ;-)
         if (true) throw new RuntimeException("THIS NEEDS TO BE UPDATED TO USE TRANSACTION_ID ADDRESSING");

         chStmt = ConnectionHandler.runPreparedQuery(queryAllRelations);
         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            row[0] = rSet.getString("type_name");
            row[1] = rSet.getString("guid_a");
            row[2] = rSet.getString("guid_b");
            row[3] = rSet.getString("order_value");
            row[4] = rSet.getString("rationale");
            excelWriter.writeRow(row);
         }
         excelWriter.endSheet();
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void writeRelationsHeader() throws IOException {
      excelWriter.startSheet("relations", 6);
      row = new String[] {"Relation Type", "Side A Guid", "Side B Guid", "Order A", "Order B", "Rationale"};
      excelWriter.writeRow(row);
   }

   public void createArtifactSheets(Branch branch) throws IOException, SQLException {
      for (ArtifactSubtypeDescriptor descriptor : configurationManager.getValidArtifactTypes(branch)) {
         createArtifactSheet(descriptor, artifactManager.getArtifactsFromSubtype(branch, descriptor));
      }
   }

   public void createArtifactSheets(Collection<Artifact> artifacts) throws IOException, SQLException {
      HashCollection<String, Artifact> hash = new HashCollection<String, Artifact>();
      for (Artifact artifact : artifacts) {
         hash.put(artifact.getArtifactTypeName(), artifact);
      }

      for (String artifactTypeName : hash.keySet()) {
         Collection<Artifact> groupedArtifacts = hash.getValues(artifactTypeName);
         createArtifactSheet(configurationManager.getArtifactSubtypeDescriptor(artifactTypeName), groupedArtifacts);
      }
   }

   private void createArtifactSheet(ArtifactSubtypeDescriptor descriptor, Collection<Artifact> artifacts) throws IOException, SQLException {
      if (artifacts.size() > 0) {
         Artifact sampleArtifact = artifacts.iterator().next();
         int columnNum = sampleArtifact.getAttributes().size();
         excelWriter.startSheet(descriptor.getName(), columnNum);
         writeArtifactHeader(descriptor, sampleArtifact.getBranch());
         for (Artifact artifact : artifacts) {
            processArtifact(artifact);
         }
         excelWriter.endSheet();
      }
   }

   private void writeArtifactHeader(ArtifactSubtypeDescriptor descriptor, Branch branch) throws IOException, SQLException {
      Collection<DynamicAttributeDescriptor> allAttributeTypes =
            configurationManager.getAttributeTypesFromArtifactType(descriptor, branch);

      int columnIndex = 0;
      row = new String[2 + allAttributeTypes.size()];
      row[columnIndex++] = "GUID";
      row[columnIndex++] = "Human Readable Id";

      for (DynamicAttributeDescriptor attributeType : allAttributeTypes) {
         row[columnIndex] = attributeType.getName();
         columnIndexHash.put(row[columnIndex], columnIndex++);
      }

      excelWriter.writeRow(row);
   }

   private void processArtifact(Artifact artifact) throws IOException, SQLException {
      Arrays.fill(row, 0, row.length, null);
      row[0] = artifact.getGuid();
      row[1] = artifact.getHumanReadableId();
      Collection<DynamicAttributeManager> attributes = artifact.getAttributes();
      for (DynamicAttributeManager attributeType : attributes) {
         row[columnIndexHash.get(attributeType.getDescriptor().getName())] = prepareAttributes(attributeType);
      }
      excelWriter.writeRow(row);
   }

   private String prepareAttributes(DynamicAttributeManager attributeType) {
      Collection<Attribute> attributes = attributeType.getAttributes();
      if (attributes.size() == 0) {
         return null;
      } else if (attributes.size() == 1) {
         return attributes.iterator().next().getStringData();
      } else {
         StringBuilder strB = new StringBuilder(200);
         for (Attribute attribute : attributes) {
            strB.append(attribute.getStringData());
            strB.append(ATTRIBUTE_VALUE_DELIMITER);
         }
         return strB.toString();
      }
   }

   public String finish() throws IOException, CoreException {
      return finish(null);
   }

   public String finish(String filename) throws IOException, CoreException {
      excelWriter.endWorkbook();
      if (filename == null) {
         IFile iFile = OseeData.getIFile("Artifact_Export_" + dateFormat.format(new Date()) + ".xml");
         AIFile.writeToFile(iFile, charBak);
         return iFile.getLocation().toOSString();
      } else {
         AFile.writeFile(filename, Lib.inputStreamToString(charBak));
         return filename;
      }
   }
}