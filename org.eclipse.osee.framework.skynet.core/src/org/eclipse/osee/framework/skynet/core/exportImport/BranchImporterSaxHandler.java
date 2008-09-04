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

package org.eclipse.osee.framework.skynet.core.exportImport;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.BranchType;
import org.eclipse.osee.framework.db.connection.core.SequenceManager;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeApplicationServerContext;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeURL;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Robert A. Fisher
 */
public class BranchImporterSaxHandler extends BranchSaxHandler {
   private static final String INSERT_ARTIFACT_VERSION =
         "INSERT INTO " + ARTIFACT_VERSION_TABLE + " (art_id, gamma_id, modification_id) VALUES (?,?,?)";
   private static final String INSERT_ATTRIBUTE =
         "INSERT INTO " + ATTRIBUTE_VERSION_TABLE + " (art_id, attr_id, attr_type_id, value, gamma_id, uri, modification_id) VALUES (?,?,?,?,?,?,?)";
   private static final String INSERT_ATTRIBUTE_GUID =
         "INSERT INTO " + ATTRIBUTE_TABLE + " (attr_id, guid) VALUES (?,?)";
   private static final String INSERT_NEW_ARTIFACT =
         "INSERT INTO " + ARTIFACT_TABLE + " (art_id, human_readable_id, art_type_id, guid) VALUES (?,?,?,?)";
   private static final String INSERT_RELATION_LINK =
         "INSERT INTO " + RELATION_LINK_VERSION_TABLE + " (rel_link_id, rel_link_type_id, a_art_id, b_art_id, a_order, b_order, rationale, gamma_id, modification_id) VALUES (?,?,?,?,?,?,?,?,?)";
   private static final String INSERT_RELATION_LINK_GUID =
         "INSERT INTO " + RELATION_LINK_TABLE + " (rel_link_id, guid) VALUES (?,?)";
   private static final String INSERT_TX_ADDRESS =
         "INSERT INTO " + TRANSACTIONS_TABLE + " (transaction_id, gamma_id, mod_type, tx_current) VALUES (?,?,?,?)";
   private static final String INSERT_TX_DETAIL =
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE + " (transaction_id, time, osee_comment, author, branch_id, commit_art_id, tx_type) VALUES (?,?,?,?,?,?,?)";

   private static final String UPDATE_BRANCH_ASSOCIATION =
         "UPDATE " + BRANCH_TABLE + " SET associated_art_id=?, branch_type=? WHERE branch_id=?";

   private final IProgressMonitor monitor;
   private final Branch supportingBranch;
   private final boolean includeMainLevelBranch;
   private final boolean includeDescendantBranches;
   private final GuidCache artifactGuidCache;
   private final GuidCache attributeGuidCache;
   private final GuidCache linkGuidCache;
   private final ZipFile binaryDataSource;

   private Integer currentTransactionId;
   private Integer currentArtifactId;
   protected Stack<Branch> curBranch;

   private int artifactOnTransactionCount;
   private int linkOnTransactionCount;
   private int transactionOnBranchCount;

   private Stack<Object> transactionKeys;

   public BranchImporterSaxHandler(ZipFile binaryDataSource, Branch supportingBranch, boolean includeMainLevelBranch, boolean includeDescendantBranches, IProgressMonitor monitor) throws SQLException, IOException {
      this.currentTransactionId = null;
      this.currentArtifactId = null;
      this.curBranch = new Stack<Branch>();

      this.artifactOnTransactionCount = 0;
      this.linkOnTransactionCount = 0;
      this.transactionOnBranchCount = 0;

      this.supportingBranch = supportingBranch;
      this.includeMainLevelBranch = includeMainLevelBranch;
      this.includeDescendantBranches = includeDescendantBranches;

      this.artifactGuidCache = new GuidCache(ARTIFACT_TABLE, "art_id");
      this.attributeGuidCache = new GuidCache(ATTRIBUTE_TABLE, "attr_id");
      this.linkGuidCache = new GuidCache(RELATION_LINK_TABLE, "rel_link_id");

      this.transactionKeys = new Stack<Object>();
      this.binaryDataSource = binaryDataSource;

      if (monitor == null) {
         monitor = new NullProgressMonitor();
      }
      this.monitor = monitor;

      this.monitor.beginTask("Parsing", IProgressMonitor.UNKNOWN);
   }

   @Override
   protected void processBranch(String name, Timestamp time, String associatedArtGuid, String branchType) throws Exception {
      if (monitor.isCanceled()) {
         return;
      }

      monitor.setTaskName("Branch " + name + " " + time);

      if (curBranch.isEmpty()) {
         if (includeMainLevelBranch) {
            curBranch.push(supportingBranch);
            transactionKeys.push(new Object());
            ConnectionHandler.startTransactionLevel(transactionKeys.peek());
         } else {
            curBranch.push(null);
         }
      } else {
         if (includeDescendantBranches) {
            Branch parentBranch = curBranch.peek();
            if (parentBranch == null) {
               parentBranch = supportingBranch;
            }

            TransactionId parentTransactionId =
                  TransactionIdManager.getInstance().getPriorTransaction(time, parentBranch);

            Branch newBranch = null;
            try {
               newBranch = BranchPersistenceManager.getBranch(name);
            } catch (IllegalArgumentException ex) {
               // We don't mind not being able to get the branch, that is the normal case for new
               // data
            }
            if (newBranch != null) {
               OseeLog.log(SkynetActivator.class, Level.WARNING, "Branch " + name + " already imported, skipping");
               curBranch.push(null);
               return;
            }

            transactionKeys.push(new Object());
            ConnectionHandler.startTransactionLevel(transactionKeys.peek());
            newBranch = BranchPersistenceManager.createWorkingBranch(parentTransactionId, null, name, null);

            BranchType branchTypeEnum = null;
            if (Strings.isValid(branchType)) {
               branchTypeEnum = BranchType.valueOf(branchType);
            }

            Integer associatedArtId = null;
            if (associatedArtGuid != null) {
               associatedArtId = artifactGuidCache.getId(associatedArtGuid);
            }
            if (associatedArtId == null) {
               associatedArtId = new Integer(-1);
            }
            ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_ASSOCIATION, associatedArtId,
                  branchTypeEnum != null ? branchTypeEnum.ordinal() : null, newBranch.getBranchId());

            curBranch.push(newBranch);
         } else {
            curBranch.push(null);
         }
      }
   }

   @Override
   protected void processBranchDone() {
      if (monitor.isCanceled()) {
         return;
      }

      Branch branch = curBranch.pop();
      if (branch != null) {
         ConnectionHandler.setTransactionLevelAsSuccessful(transactionKeys.peek());
         ConnectionHandler.endTransactionLevel(transactionKeys.pop());
         postBranchImportProcessing(branch);
      }
      transactionOnBranchCount = 0;
   }

   private void postBranchImportProcessing(Branch branch) {
      StringBuffer response = new StringBuffer();
      long start = System.currentTimeMillis();
      Connection connection = null;
      try {
         boolean tagAfterBranchImport = false;
         connection = ConnectionHandler.getConnection();
         switch (SupportedDatabase.getDatabaseType(connection)) {
            case oracle:
               tagAfterBranchImport = true;
               break;
            default:
               OseeLog.log(
                     SkynetActivator.class,
                     Level.WARNING,
                     String.format(
                           "Tagging during branch import is not supported. " + "Quick searches may be out of date for branch [%s]. Run >tag_all %d in console at a later time.",
                           branch.getBranchName(), branch.getBranchId()));
               break;
         }

         if (tagAfterBranchImport) {
            OseeLog.log(SkynetActivator.class, Level.INFO, String.format("Tagging [%s] branch", branch.getBranchName()));
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("branchId", Integer.toString(branch.getBranchId()));
            parameters.put("wait", "true");
            String url =
                  HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeApplicationServerContext.SEARCH_TAGGING_CONTEXT,
                        parameters);
            response.append(HttpProcessor.post(new URL(url)));
            OseeLog.log(SkynetActivator.class, Level.INFO, response.toString());
            OseeLog.log(SkynetActivator.class, Level.INFO, String.format("Tagging Completed in [%d ms]",
                  System.currentTimeMillis() - start));
         }
      } catch (Exception ex) {
         if (response.length() > 0) {
            response.append("\n");
         }
         response.append(ex.getLocalizedMessage());
         OseeLog.log(SkynetActivator.class, Level.SEVERE, response.toString(), ex);
      } finally {
         response.delete(0, response.length());
         if (connection != null) {
            try {
               connection.close();
            } catch (SQLException ex) {
               OseeLog.log(SkynetActivator.class, Level.SEVERE, response.toString(), ex);
            }
         }
      }
   }

   @Override
   protected void processTransaction(String author, Timestamp time, String comment, String commitArtGuid, Integer txType) throws SQLException {
      // Skip transaction records if the current branch is not being included
      if (curBranch.peek() == null || monitor.isCanceled()) {
         return;
      }

      monitor.subTask("Transaction " + ++transactionOnBranchCount);
      currentTransactionId = SequenceManager.getNextTransactionId();
      Integer authorId = artifactGuidCache.getId(author);
      Integer commitArtId = artifactGuidCache.getId(commitArtGuid);

      ConnectionHandler.runPreparedUpdate(INSERT_TX_DETAIL,
            currentTransactionId != null ? currentTransactionId : SQL3DataType.INTEGER,
            time != null ? time : SQL3DataType.TIMESTAMP, comment != null ? comment : SQL3DataType.VARCHAR,
            authorId == null ? -1 : authorId, curBranch.peek().getBranchId(),
            commitArtId != null ? commitArtId : SQL3DataType.INTEGER, txType != null ? txType : SQL3DataType.INTEGER);
   }

   @Override
   protected void processTransactionDone() {
      currentTransactionId = null;
      this.artifactOnTransactionCount = 0;
      this.linkOnTransactionCount = 0;
   }

   @Override
   protected void processArtifact(String guid, String artifactTypeName, String hrid, String modType, int txCurrent) throws SQLException {
      if (monitor.isCanceled()) {
         return;
      }

      // Skip this artifact if the transaction is not being included
      if (currentTransactionId == null) {
         return;
      }

      try {
         monitor.subTask("Transaction " + transactionOnBranchCount + " Artifact " + ++artifactOnTransactionCount);
         currentArtifactId = artifactGuidCache.getId(guid);

         ModificationType modificationType = null;
         if (Strings.isValid(modType)) {
            modificationType = ModificationType.valueOf(modType);
         }

         // New artifact
         if (currentArtifactId == null) {
            if (modificationType != null && modificationType.equals(ModificationType.DELETED)) {
               OseeLog.log(SkynetActivator.class, Level.WARNING,
                     "Initial creation of artifact " + hrid + " was a delete version");
            }
            currentArtifactId = SequenceManager.getNextArtifactId();

            ArtifactType artifactType = ArtifactTypeManager.getType(artifactTypeName);
            int artTypeId = artifactType.getArtTypeId();
            artifactGuidCache.map(currentArtifactId, guid);
            ConnectionHandler.runPreparedUpdate(INSERT_NEW_ARTIFACT, currentArtifactId, hrid, artTypeId, guid);
         }

         int gammaId = SequenceManager.getNextGammaId();
         int modificationInt = modificationType != null ? modificationType.getValue() : -1;
         ConnectionHandler.runPreparedUpdate(INSERT_ARTIFACT_VERSION, currentArtifactId, gammaId, modificationInt);
         insertTxAddress(gammaId, modificationInt, txCurrent);
      } catch (IllegalArgumentException ex) {
         OseeLog.log(SkynetActivator.class, Level.SEVERE, ex);
      }
   }

   @Override
   protected void processArtifactDone() {
      currentArtifactId = null;
   }

   @Override
   protected void processAttribute(String artifactHrid, String attributeGuid, String attributeTypeName, String stringValue, String uriValue, String modType, int txCurrent) throws Exception {
      // Skip this attribute if the artifact is not being included
      if (currentArtifactId == null || monitor.isCanceled()) {
         return;
      }

      Integer attrId = attributeGuidCache.getId(attributeGuid);
      if (attrId == null) {
         attrId = SequenceManager.getNextAttributeId();
         attributeGuidCache.map(attrId, attributeGuid);
         ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE_GUID, attrId, attributeGuid);
      }
      AttributeType attributeType = AttributeTypeManager.getType(attributeTypeName);
      int attrTypeId = attributeType.getAttrTypeId();
      int gammaId = SequenceManager.getNextGammaId();

      ModificationType modificationType = null;
      if (Strings.isValid(modType)) {
         modificationType = ModificationType.valueOf(modType);
      }

      String uriToStore = null;
      if (Strings.isValid(uriValue)) {
         InputStream inputStream = null;
         try {
            ZipFile zipFile = getBinaryDataSource();
            ZipEntry entry = zipFile.getEntry(uriValue);
            inputStream = zipFile.getInputStream(entry);
            URL url = AttributeURL.getStorageURL(gammaId, artifactHrid, Lib.getExtension(uriValue));
            URI result =
                  HttpProcessor.save(url, inputStream, HttpURLConnection.guessContentTypeFromName(uriValue),
                        "ISO-8859-1");
            uriToStore = result.toASCIIString();
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }
      } else {
         uriToStore = "";
      }
      int modificationInt = modificationType != null ? modificationType.getValue() : -1;
      ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE, currentArtifactId, attrId, attrTypeId,
            stringValue != null ? stringValue : SQL3DataType.VARCHAR, gammaId,
            uriToStore != null ? uriToStore : SQL3DataType.VARCHAR, modificationInt);
      insertTxAddress(gammaId, modificationInt, txCurrent);
   }

   @Override
   protected void processLink(String guid, String type, String aguid, String bguid, String aOrder, String bOrder, String rationale, String modType, int txCurrent) throws SQLException {
      // Skip this link if the transaction is not being included
      if (currentTransactionId == null || monitor.isCanceled()) {
         return;
      }

      monitor.subTask("Transaction " + transactionOnBranchCount + " Link " + ++linkOnTransactionCount);
      Integer relLinkId = linkGuidCache.getId(guid);
      if (relLinkId == null) {
         relLinkId = SequenceManager.getNextRelationId();
         linkGuidCache.map(relLinkId, guid);
         ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK_GUID, relLinkId, guid);
      }

      int relLinkTypeId = RelationTypeManager.getType(type).getRelationTypeId();

      Integer aArtId = getSideArtId(guid, aguid, true);
      Integer bArtId = getSideArtId(guid, bguid, false);
      if (aArtId == null || bArtId == null) {
         OseeLog.log(SkynetActivator.class, Level.WARNING, "Link not imported");
         return;
      }

      Integer aOrderId = getOrderArtId(guid, aOrder, true);
      Integer bOrderId = getOrderArtId(guid, bOrder, false);

      ModificationType modificationType = null;
      if (Strings.isValid(modType)) {
         modificationType = ModificationType.valueOf(modType);
      }
      int gammaId = SequenceManager.getNextGammaId();
      int modificationInt = modificationType != null ? modificationType.getValue() : -1;
      ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK, relLinkId, relLinkTypeId, aArtId, bArtId, aOrderId,
            bOrderId, rationale, gammaId, modificationInt);
      insertTxAddress(gammaId, modificationInt, txCurrent);
   }

   private Integer getSideArtId(String linkGuid, String nodeGuid, boolean isSideA) {
      Integer toReturn = artifactGuidCache.getId(nodeGuid);
      if (toReturn == null) {
         OseeLog.log(SkynetActivator.class, Level.WARNING, String.format(
               "Link [%s] order [%s guid]=[%s] could not be resolved to an artId.", linkGuid, isSideA ? "A" : "B",
               nodeGuid));
      }
      return toReturn;
   }

   private Integer getOrderArtId(String linkGuid, String orderGuid, boolean isSideA) {
      Integer toReturn = null;
      if (Strings.isValid(orderGuid)) {
         toReturn = artifactGuidCache.getId(orderGuid);
         if (toReturn == null) {
            OseeLog.log(SkynetActivator.class, Level.WARNING, String.format(
                  "Link [%s] order [%s guid order]=[%s] could not be resolved to an artId.", linkGuid,
                  isSideA ? "A" : "B", orderGuid));
            toReturn = new Integer(-2);
         }
      } else {
         toReturn = new Integer(-2);
      }
      return toReturn;
   }

   private void insertTxAddress(int gammaId, int modType, int txCurrent) throws SQLException {
      ConnectionHandler.runPreparedUpdate(INSERT_TX_ADDRESS, currentTransactionId, gammaId, modType, txCurrent);
   }

   private ZipFile getBinaryDataSource() {
      return binaryDataSource;
   }
}
