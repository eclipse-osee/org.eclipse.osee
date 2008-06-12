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
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ART_ID_SEQ;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTR_ID_SEQ;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.GAMMA_ID_SEQ;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.REL_LINK_ID_SEQ;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_ID_SEQ;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.utils.AttributeURL;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Robert A. Fisher
 */
public class BranchImporterSaxHandler extends BranchSaxHandler {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchImporterSaxHandler.class);
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private static final TransactionIdManager transactionManager = TransactionIdManager.getInstance();

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
         "INSERT INTO " + TRANSACTION_DETAIL_TABLE + " (transaction_id, time, osee_comment, author, branch_id) VALUES (?,?,?,?,?)";

   private static final String UPDATE_BRANCH_ASSOCIATION =
         "UPDATE " + BRANCH_TABLE + " SET associated_art_id=? WHERE branch_id=?";

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
   protected void processBranch(String name, Timestamp time, String associatedArtGuid) throws Exception {
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

            TransactionId parentTransactionId = transactionManager.getPriorTransaction(time, parentBranch);

            Branch newBranch = null;
            try {
               newBranch = branchManager.getBranch(name);
            } catch (IllegalArgumentException ex) {
               // We don't mind not being able to get the branch, that is the normal case for new
               // data
            }
            if (newBranch != null) {
               logger.log(Level.WARNING, "Branch " + name + " already imported, skipping");
               curBranch.push(null);
               return;
            }

            transactionKeys.push(new Object());
            ConnectionHandler.startTransactionLevel(transactionKeys.peek());
            newBranch = branchManager.createWorkingBranch(parentTransactionId, null, name, null);

            // Fix the associatedArtId if it is available
            if (associatedArtGuid != null) {
               Integer associatedArtId = null;
               associatedArtId = artifactGuidCache.getId(associatedArtGuid);

               if (associatedArtId != null) {
                  ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_ASSOCIATION, SQL3DataType.INTEGER, associatedArtId,
                        SQL3DataType.INTEGER, newBranch.getBranchId());
               }
            }

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
      }
      transactionOnBranchCount = 0;
   }

   @Override
   protected void processTransaction(String author, Timestamp time, String comment) throws SQLException {
      // Skip transaction records if the current branch is not being included
      if (curBranch.peek() == null || monitor.isCanceled()) {
         return;
      }

      monitor.subTask("Transaction " + ++transactionOnBranchCount);
      currentTransactionId = Query.getNextSeqVal(TRANSACTION_ID_SEQ);
      Integer authorId = artifactGuidCache.getId(author);

      ConnectionHandler.runPreparedUpdate(INSERT_TX_DETAIL, SQL3DataType.INTEGER, currentTransactionId,
            SQL3DataType.TIMESTAMP, time, SQL3DataType.VARCHAR, comment, SQL3DataType.INTEGER,
            authorId == null ? -1 : authorId, SQL3DataType.INTEGER, curBranch.peek().getBranchId());

      transactionManager.updateEditableTransactionId(currentTransactionId, curBranch.peek());
   }

   @Override
   protected void processTransactionDone() {
      currentTransactionId = null;
      this.artifactOnTransactionCount = 0;
      this.linkOnTransactionCount = 0;
   }

   @Override
   protected void processArtifact(String guid, String artifactTypeName, String hrid, boolean deleted) throws SQLException {
      if (monitor.isCanceled()) {
         return;
      }

      // Skip this artifact if the transaction is not being included
      if (currentTransactionId == null) {
         return;
      }

      monitor.subTask("Transaction " + transactionOnBranchCount + " Artifact " + ++artifactOnTransactionCount);
      boolean modified = true;
      currentArtifactId = artifactGuidCache.getId(guid);
      // New artifact
      if (currentArtifactId == null) {
         modified = false;
         if (deleted) {
            logger.log(Level.WARNING, "Initial creation of artifact " + hrid + " was a delete version");
         }
         currentArtifactId = Query.getNextSeqVal(ART_ID_SEQ);

         ArtifactType artifactType = ArtifactTypeManager.getType(artifactTypeName);
         int artTypeId = artifactType.getArtTypeId();
         artifactGuidCache.map(currentArtifactId, guid);
         ConnectionHandler.runPreparedUpdate(INSERT_NEW_ARTIFACT, SQL3DataType.INTEGER, currentArtifactId,
               SQL3DataType.VARCHAR, hrid, SQL3DataType.INTEGER, artTypeId, SQL3DataType.VARCHAR, guid);
      }

      int gammaId = Query.getNextSeqVal(GAMMA_ID_SEQ);
      ModificationType modificationType = getModType(modified, deleted);

      ConnectionHandler.runPreparedUpdate(INSERT_ARTIFACT_VERSION, SQL3DataType.INTEGER, currentArtifactId,
            SQL3DataType.VARCHAR, gammaId, SQL3DataType.INTEGER, modificationType.getValue());
      insertTxAddress(gammaId, modificationType.getValue(), TxChange.CURRENT.getValue());
   }

   @Override
   protected void processArtifactDone() {
      currentArtifactId = null;
   }

   @Override
   protected void processAttribute(String artifactHrid, String attributeGuid, String attributeTypeName, String stringValue, String uriValue, boolean deleted) throws Exception {
      // Skip this attribute if the artifact is not being included
      if (currentArtifactId == null || monitor.isCanceled()) {
         return;
      }

      boolean modified = true;
      Integer attrId = attributeGuidCache.getId(attributeGuid);
      if (attrId == null) {
         modified = false;

         attrId = Query.getNextSeqVal(ATTR_ID_SEQ);
         attributeGuidCache.map(attrId, attributeGuid);
         ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE_GUID, SQL3DataType.INTEGER, attrId, SQL3DataType.VARCHAR,
               attributeGuid);
      }

      int attrTypeId = AttributeTypeManager.getType(attributeTypeName).getAttrTypeId();
      int gammaId = Query.getNextSeqVal(GAMMA_ID_SEQ);
      ModificationType modificationType = getModType(modified, deleted);

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
      ConnectionHandler.runPreparedUpdate(INSERT_ATTRIBUTE, SQL3DataType.INTEGER, currentArtifactId,
            SQL3DataType.INTEGER, attrId, SQL3DataType.INTEGER, attrTypeId, SQL3DataType.VARCHAR, stringValue,
            SQL3DataType.INTEGER, gammaId, SQL3DataType.VARCHAR, uriToStore, SQL3DataType.INTEGER,
            modificationType.getValue());
      insertTxAddress(gammaId, modificationType.getValue(), TxChange.CURRENT.getValue());
   }

   @Override
   protected void processLink(String guid, String type, String aguid, String bguid, int aOrder, int bOrder, String rationale, boolean deleted) throws SQLException {
      // Skip this link if the transaction is not being included
      if (currentTransactionId == null || monitor.isCanceled()) {
         return;
      }

      boolean modified = true;

      monitor.subTask("Transaction " + transactionOnBranchCount + " Link " + ++linkOnTransactionCount);
      Integer relLinkId = linkGuidCache.getId(guid);
      if (relLinkId == null) {
         modified = false;

         relLinkId = Query.getNextSeqVal(REL_LINK_ID_SEQ);
         linkGuidCache.map(relLinkId, guid);
         ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK_GUID, SQL3DataType.INTEGER, relLinkId,
               SQL3DataType.VARCHAR, guid);
      }

      int relLinkTypeId = RelationTypeManager.getType(type).getRelationTypeId();

      Integer aArtId = artifactGuidCache.getId(aguid);
      if (aArtId == null) {
         logger.log(Level.WARNING,
               "Link " + guid + " side A guid " + aguid + " could not be resolved to an artId. Link not imported");
         return;
      }
      Integer bArtId = artifactGuidCache.getId(bguid);
      if (bArtId == null) {
         logger.log(Level.WARNING,
               "Link " + guid + " side B guid " + bguid + " could not be resolved to an artId. Link not imported");
         return;
      }
      int gammaId = Query.getNextSeqVal(GAMMA_ID_SEQ);
      ModificationType modificationType = getModType(modified, deleted);

      ConnectionHandler.runPreparedUpdate(INSERT_RELATION_LINK, SQL3DataType.INTEGER, relLinkId, SQL3DataType.INTEGER,
            relLinkTypeId, SQL3DataType.INTEGER, aArtId, SQL3DataType.INTEGER, bArtId, SQL3DataType.INTEGER, aOrder,
            SQL3DataType.INTEGER, bOrder, SQL3DataType.VARCHAR, rationale, SQL3DataType.INTEGER, gammaId,
            SQL3DataType.INTEGER, modificationType.getValue());
      insertTxAddress(gammaId, modificationType.getValue(), TxChange.CURRENT.getValue());
   }

   private void insertTxAddress(int gammaId, int modType, int txCurrent) throws SQLException {
      ConnectionHandler.runPreparedUpdate(INSERT_TX_ADDRESS, SQL3DataType.INTEGER, currentTransactionId,
            SQL3DataType.INTEGER, gammaId, SQL3DataType.INTEGER, modType, SQL3DataType.INTEGER, txCurrent);
   }

   private ModificationType getModType(boolean modified, boolean deleted) {
      return deleted ? ModificationType.DELETED : (modified ? ModificationType.CHANGE : ModificationType.NEW);
   }

   private ZipFile getBinaryDataSource() {
      return binaryDataSource;
   }
}
