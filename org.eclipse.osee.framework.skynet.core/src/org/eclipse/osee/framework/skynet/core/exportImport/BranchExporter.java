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
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_TYPE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_TYPE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.BRANCH_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_TYPE_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.core.BranchType;
import org.eclipse.osee.framework.db.connection.core.RsetProcessor;
import org.eclipse.osee.framework.db.connection.core.query.Query;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeApplicationServerContext;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Robert A. Fisher
 */
public class BranchExporter {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(BranchExporter.class);

   private static final LocalAliasTable ARTIFACT_ALIAS_1 = ARTIFACT_TABLE.aliasAs("art1");
   private static final LocalAliasTable ARTIFACT_ALIAS_2 = ARTIFACT_TABLE.aliasAs("art2");
   private static final LocalAliasTable TX_DETAIL_ALIAS_1 = TRANSACTION_DETAIL_TABLE.aliasAs("txd1");
   private static final LocalAliasTable TX_DETAIL_ALIAS_2 = TRANSACTION_DETAIL_TABLE.aliasAs("txd2");

   private static final String SELECT_CHILD_BRANCHES =
         "SELECT " + BRANCH_TABLE.columns("branch_id", "branch_name", "associated_art_id", "branch_type") + "," + TX_DETAIL_ALIAS_1.columns(
               "osee_comment", "time") + " FROM " + BRANCH_TABLE + "," + TX_DETAIL_ALIAS_1 + " WHERE " + BRANCH_TABLE.column("archived") + "=0 AND " + BRANCH_TABLE.column("parent_branch_id") + "=? AND " + BRANCH_TABLE.join(
               TX_DETAIL_ALIAS_1, "branch_id") + " AND " + TX_DETAIL_ALIAS_1.column("transaction_id") + "=(SELECT MIN(" + TX_DETAIL_ALIAS_2.column("transaction_id") + ") FROM " + TX_DETAIL_ALIAS_2 + " WHERE " + TX_DETAIL_ALIAS_2.join(
               BRANCH_TABLE, "branch_id") + ")";
   private static final String SELECT_TRANSACTIONS_STATS =
         "SELECT " + TRANSACTION_DETAIL_TABLE.min("transaction_id", "min_transaction") + "," + TRANSACTION_DETAIL_TABLE.max(
               "transaction_id", "max_transaction") + "," + "COUNT(*) AS transaction_count FROM " + TRANSACTION_DETAIL_TABLE + " WHERE branch_id=? AND time>? AND time<=?";
   private static final String SELECT_ARTIFACTS =
         "SELECT " + ARTIFACT_TABLE.columns("art_id", "guid", "human_readable_id") + "," + ARTIFACT_TYPE_TABLE.column("name") + "," + ARTIFACT_VERSION_TABLE.column("modification_id") + "," + TRANSACTION_DETAIL_TABLE.columns(
               "transaction_id", "osee_comment", "time", "author", "commit_art_id", "tx_type") + "," + TRANSACTIONS_TABLE.columns(
               "tx_current", "mod_type") + " FROM " + TRANSACTION_DETAIL_TABLE + "," + TRANSACTIONS_TABLE + "," + ARTIFACT_VERSION_TABLE + "," + ARTIFACT_TABLE + "," + ARTIFACT_TYPE_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ">=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=? AND " + TRANSACTION_DETAIL_TABLE.join(
               TRANSACTIONS_TABLE, "transaction_id") + " AND " + TRANSACTIONS_TABLE.join(ARTIFACT_VERSION_TABLE,
               "gamma_id") + " AND " + ARTIFACT_VERSION_TABLE.join(ARTIFACT_TABLE, "art_id") + " AND " + ARTIFACT_TABLE.join(
               ARTIFACT_TYPE_TABLE, "art_type_id") + " ORDER BY " + TRANSACTION_DETAIL_TABLE.column("transaction_id");
   private static final String SELECT_ATTRIBUTES =
         "SELECT " + ATTRIBUTE_VERSION_TABLE.columns("attr_id", "art_id", "modification_id", "value", "uri") + "," + ATTRIBUTE_TYPE_TABLE.column("name") + "," + TRANSACTION_DETAIL_TABLE.columns(
               "transaction_id", "osee_comment", "time", "author", "commit_art_id", "tx_type") + "," + TRANSACTIONS_TABLE.columns(
               "tx_current", "mod_type") + " FROM " + TRANSACTION_DETAIL_TABLE + "," + TRANSACTIONS_TABLE + "," + ATTRIBUTE_VERSION_TABLE + "," + ATTRIBUTE_TYPE_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ">=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=? AND " + TRANSACTION_DETAIL_TABLE.join(
               TRANSACTIONS_TABLE, "transaction_id") + " AND " + TRANSACTIONS_TABLE.join(ATTRIBUTE_VERSION_TABLE,
               "gamma_id") + " AND " + ATTRIBUTE_VERSION_TABLE.join(ATTRIBUTE_TYPE_TABLE, "attr_type_id") + " ORDER BY " + TRANSACTION_DETAIL_TABLE.column("transaction_id");
   private static final String SELECT_LINKS =
         "SELECT " + RELATION_LINK_VERSION_TABLE.columns("rel_link_id", "a_order", "b_order", "rationale",
               "modification_id") + "," + RELATION_LINK_TYPE_TABLE.columns("type_name") + "," + ARTIFACT_ALIAS_1.column("guid as a_guid") + "," + ARTIFACT_ALIAS_2.column("guid as b_guid") + "," + TRANSACTION_DETAIL_TABLE.columns(
               "transaction_id", "osee_comment", "time", "author", "commit_art_id", "tx_type") + "," + TRANSACTIONS_TABLE.columns(
               "tx_current", "mod_type") + " FROM " + TRANSACTION_DETAIL_TABLE + "," + TRANSACTIONS_TABLE + "," + RELATION_LINK_VERSION_TABLE + "," + RELATION_LINK_TYPE_TABLE + "," + ARTIFACT_ALIAS_1 + "," + ARTIFACT_ALIAS_2 + " WHERE " + TRANSACTION_DETAIL_TABLE.column("branch_id") + "=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + ">=? AND " + TRANSACTION_DETAIL_TABLE.column("transaction_id") + "<=? AND " + TRANSACTION_DETAIL_TABLE.join(
               TRANSACTIONS_TABLE, "transaction_id") + " AND " + TRANSACTIONS_TABLE.join(RELATION_LINK_VERSION_TABLE,
               "gamma_id") + " AND " + RELATION_LINK_VERSION_TABLE.join(RELATION_LINK_TYPE_TABLE, "rel_link_type_id") + " AND " + RELATION_LINK_VERSION_TABLE.column("a_art_id") + "=" + ARTIFACT_ALIAS_1.column("art_id") + " AND " + RELATION_LINK_VERSION_TABLE.column("b_art_id") + "=" + ARTIFACT_ALIAS_2.column("art_id") + " ORDER BY " + TRANSACTION_DETAIL_TABLE.column("transaction_id");

   private static final RsetProcessor<ArtifactData> ARTIFACT_PROCESSOR = new ArtifactProcessor();
   private static final RsetProcessor<LinkData> LINK_PROCESSOR = new LinkProcessor();

   private final RsetProcessor<AttributeData> ATTRIBUTE_PROCESSOR;
   private final RsetProcessor<BranchData> BRANCH_PROCESSOR;

   private final GuidCache artGuidCache;
   private final GuidCache attrGuidCache;
   private final GuidCache relLinkGuidCache;
   private final IProgressMonitor monitor;
   private final File file;
   private final Branch branch;
   private final Timestamp from;
   private final Timestamp to;
   private final boolean descendantsOnly;
   private final long startMillis;
   private final HashMap<String, String> typeNameMap;

   /**
    * @param file
    * @param branch
    * @throws SQLException
    * @throws IOException
    */
   public BranchExporter(File file, Branch branch, Timestamp from, Timestamp to, boolean descendantsOnly) throws SQLException, IOException {
      this(null, file, branch, from, to, descendantsOnly);
   }

   /**
    * @param monitor
    * @param file
    * @param branch
    * @throws SQLException
    * @throws IOException
    */
   public BranchExporter(IProgressMonitor monitor, File file, Branch branch, Timestamp from, Timestamp to, boolean descendantsOnly) throws SQLException, IOException {
      super();
      this.startMillis = System.currentTimeMillis();
      this.monitor = monitor == null ? new NullProgressMonitor() : monitor;
      this.file = file;
      this.branch = branch;
      this.from = from;
      this.to = to;
      this.descendantsOnly = descendantsOnly;
      this.artGuidCache = new GuidCache(ARTIFACT_TABLE, "art_id");
      this.attrGuidCache = new GuidCache(ATTRIBUTE_TABLE, "attr_id", true);
      this.relLinkGuidCache = new GuidCache(RELATION_LINK_TABLE, "rel_link_id", true);

      this.ATTRIBUTE_PROCESSOR = new AttributeProcessor();
      this.BRANCH_PROCESSOR = new BranchProcessor();
      this.typeNameMap = new HashMap<String, String>();
   }

   public void export() throws Exception {
      String baseName = Lib.removeExtension(file.getName());

      File rootDirectory = new File(file.getParentFile(), baseName);
      rootDirectory.mkdirs();

      File indexFile = new File(rootDirectory, "branch.data.xml");
      Writer writer =
            new BufferedWriter(new OutputStreamWriter(new FileOutputStream(indexFile), "UTF-8"), (int) Math.pow(2, 24));
      writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n");

      processBranch(rootDirectory, writer, new BranchData(branch), true, !descendantsOnly);

      writer.close();
      attrGuidCache.finalizeCachedGuids();
      relLinkGuidCache.finalizeCachedGuids();

      // Compressed and Clean-up
      logger.log(Level.INFO, "Compressing Branch Data into " + baseName + ".zip");
      File zipTarget = new File(file.getParent(), baseName + ".zip");
      Lib.compressDirectory(rootDirectory, zipTarget.getAbsolutePath(), true);
      Lib.deleteDir(rootDirectory);
      logger.log(
            Level.INFO,
            "Branch export of " + branch.getBranchName() + " and children finished in " + Lib.getElapseString(startMillis));
   }

   private void processBranch(File rootDirectory, Writer writer, BranchData branch, boolean mainLevel, boolean useTheseTransactions) throws Exception {
      Collection<BranchData> childBranches = new LinkedList<BranchData>();
      HashCollection<Integer, BranchData> childBranchMap = new HashCollection<Integer, BranchData>();

      ConnectionHandlerStatement artChStmt = null;
      ConnectionHandlerStatement attrChStmt = null;
      ConnectionHandlerStatement linkChStmt = null;
      ConnectionHandlerStatement statStmt = null;

      writer.write(String.format(
            "<Branch time=\"%s\"%s%s>\n",
            branch.getTime().toString(),
            branch.getAssociatedArtGuid() != null ? String.format(" associated_guid=\"%s\" ", branch.associatedArtGuid) : "",
            branch.getBranchType() != null ? String.format(" branchType=\"%s\" ", branch.getBranchType().name()) : ""));
      writer.write("<Name>");
      Xml.writeAsCdata(writer, branch.getName());
      writer.write("</Name>\n");

      int transactionsProcessed = 0;
      try {
         monitor.setTaskName("Acquiring transactions for branch " + branch.getName());

         statStmt = ConnectionHandler.runPreparedQuery(SELECT_TRANSACTIONS_STATS, branch.getBranchId(), from, to);

         ResultSet statSet = statStmt.getRset();
         statSet.next(); // All aggregate returns, guaranteed 1 row
         int totalTransactions = statSet.getInt("transaction_count");
         int startTransaction = statSet.getInt("min_transaction");
         int endTransaction = statSet.getInt("max_transaction");
         writer.write(String.format(
               "<Expectation branch_id=\"%d\" transactions=\"%d\" min_transaction=\"%d\" max_transaction=\"%d\"/>\n",
               branch.getBranchId(), totalTransactions, startTransaction, endTransaction));

         monitor.subTask("Acquiring artifact data");
         artChStmt =
               ConnectionHandler.runPreparedQuery(4000, SELECT_ARTIFACTS, branch.getBranchId(), startTransaction,
                     endTransaction);
         monitor.subTask("Acquiring attribute data");
         attrChStmt =
               ConnectionHandler.runPreparedQuery(4000, SELECT_ATTRIBUTES, branch.getBranchId(), startTransaction,
                     endTransaction);
         monitor.subTask("Acquiring relation link data");
         linkChStmt =
               ConnectionHandler.runPreparedQuery(4000, SELECT_LINKS, branch.getBranchId(), startTransaction,
                     endTransaction);

         RSetHelper artSet = new RSetHelper(artChStmt.getRset());
         RSetHelper attrSet = new RSetHelper(attrChStmt.getRset());
         RSetHelper linkSet = new RSetHelper(linkChStmt.getRset());

         monitor.setTaskName("Acquiring child branches of branch " + branch.getName());
         Query.acquireCollection(childBranches, BRANCH_PROCESSOR, SELECT_CHILD_BRANCHES, branch.getBranchId());

         for (BranchData childBranch : childBranches) {
            childBranchMap.put(childBranch.baseParentTransactionId, childBranch);
         }

         int count = 0;
         totalTransactions -= mainLevel ? 0 : 1;

         if (mainLevel) {
            monitor.beginTask("Writing details", totalTransactions);
         } else {
            monitor.setTaskName("Writing details");
         }

         boolean needNext = mainLevel;
         int curTransactionId;
         while (!(artSet.isEmpty() && attrSet.isEmpty() && linkSet.isEmpty())) {
            curTransactionId = getMinTransaction(artSet, attrSet, linkSet);

            // Skip the baseline transactions of exported child branches
            if (!needNext) {
               skipTransaction(curTransactionId, artSet, attrSet, linkSet);
               curTransactionId = getMinTransaction(artSet, attrSet, linkSet);
               needNext = true;
               continue;
            }

            String task =
                  "Processing transaction " + (++count) + " of " + totalTransactions + " for branch " + branch.getName();
            monitor.subTask(task);

            if (useTheseTransactions) {
               processTransaction(rootDirectory, writer,
                     new TransactionData(curTransactionId, artSet, attrSet, linkSet), task);
               transactionsProcessed++;
            } else {
               skipTransaction(curTransactionId, artSet, attrSet, linkSet);
            }

            if (mainLevel) {
               monitor.worked(1);
            }

            Collection<BranchData> branchedBranches = childBranchMap.getValues(curTransactionId);
            if (branchedBranches != null) {
               for (BranchData branchedBranch : branchedBranches) {
                  processBranch(rootDirectory, writer, branchedBranch, false, true);
               }
            }
            if (monitor.isCanceled()) break;
         }

      } finally {
         writer.write(String.format("<Realization transactions=\"%d\"/>\n", transactionsProcessed));
         writer.write("</Branch>\n");
         DbUtil.close(artChStmt);
         DbUtil.close(attrChStmt);
         DbUtil.close(linkChStmt);
         DbUtil.close(statStmt);
      }
   }

   /**
    * @param artSet
    * @param attrSet
    * @param linkSet
    * @throws SQLException
    */
   private void skipTransaction(int curTransactionId, RSetHelper artSet, RSetHelper attrSet, RSetHelper linkSet) throws SQLException {
      while (artSet.onTransaction(curTransactionId)) {
         artSet.next();
      }
      while (attrSet.onTransaction(curTransactionId)) {
         attrSet.next();
      }
      while (linkSet.onTransaction(curTransactionId)) {
         linkSet.next();
      }
   }

   /**
    * @param artSet
    * @param attrSet
    * @param linkSet
    * @return The minimum transactionId from the non-empty result sets
    * @throws SQLException
    */
   private int getMinTransaction(RSetHelper artSet, RSetHelper attrSet, RSetHelper linkSet) throws SQLException {
      int artTran = artSet.isEmpty() ? Integer.MAX_VALUE : artSet.getRset().getInt("transaction_id");
      int attrTran = attrSet.isEmpty() ? Integer.MAX_VALUE : attrSet.getRset().getInt("transaction_id");
      int linkTran = linkSet.isEmpty() ? Integer.MAX_VALUE : linkSet.getRset().getInt("transaction_id");

      return Math.min(artTran, Math.min(attrTran, linkTran));
   }

   private void processTransaction(File rootDirectory, Writer writer, TransactionData transaction, String task) throws Exception {
      Collection<ArtifactData> artifacts = new LinkedList<ArtifactData>();
      Collection<LinkData> links = new LinkedList<LinkData>();
      HashCollection<Integer, AttributeData> attributeMap = new HashCollection<Integer, AttributeData>();

      writer.write(String.format("<Transaction author=\"%s\" time=\"%s\" commitArtGuid=\"%s\" txType=\"%s\">\n",
            transaction.getAuthorGuid(), transaction.getTime().toString(), transaction.getCommitArtGuid(),
            transaction.getTxType()));

      String transactionComment = transaction.getComment();
      if (transactionComment != null && transactionComment.length() > 0) {
         writer.write("<Comment>");
         Xml.writeAsCdata(writer, transactionComment);
         writer.write("</Comment>");
      }

      try {
         RSetHelper feeder;
         monitor.setTaskName("Acquiring artifact changes for transaction");
         feeder = transaction.getArtSet();
         while (feeder.onTransaction(transaction.getTransactionId())) {
            artifacts.add(ARTIFACT_PROCESSOR.process(feeder.getRset()));
            feeder.next();
         }
         monitor.setTaskName("Acquiring attribute changes for transaction");
         feeder = transaction.getAttrSet();
         AttributeData attrData;
         while (feeder.onTransaction(transaction.getTransactionId())) {
            attrData = ATTRIBUTE_PROCESSOR.process(feeder.getRset());
            attributeMap.put(attrData.getArtId(), attrData);
            feeder.next();
         }

         int count = 0;
         int total = artifacts.size();
         String artifactTask;
         for (ArtifactData artifact : artifacts) {
            artifactTask = task + " Artifact " + (++count) + " of " + total;
            monitor.subTask(task);

            processArtifact(rootDirectory, writer, artifact, attributeMap, artifactTask);

            if (monitor.isCanceled()) return;
         }

         monitor.setTaskName("Acquiring link changes for transaction");
         feeder = transaction.getLinkSet();
         while (feeder.onTransaction(transaction.getTransactionId())) {
            links.add(LINK_PROCESSOR.process(feeder.getRset()));
            feeder.next();
         }

         count = 0;
         total = links.size();
         for (LinkData link : links) {
            monitor.subTask(task + " Link " + (++count) + " of " + total);

            processLink(link, writer);

            if (monitor.isCanceled()) return;
         }
      } finally {
         writer.write("</Transaction>\n");
      }
   }

   private void processArtifact(File rootDirectory, Writer writer, ArtifactData artifact, HashCollection<Integer, AttributeData> attributeMap, String task) throws Exception {

      writer.write(String.format("<Artifact guid=\"%s\" type=\"%s\" hrid=\"%s\" modType=\"%s\" txCurrent=\"%s\">\n",
            artifact.getGuid(), getTypeName(artifact), artifact.getHrid(), artifact.getModificationType().name(),
            artifact.getTxCurrent()));

      Collection<AttributeData> attributes = attributeMap.getValues(artifact.getArtId());
      if (attributes != null) {
         for (AttributeData attribute : attributes) {
            writer.write(String.format("<Attribute type=\"%s\" guid=\"%s\" modType=\"%s\" txCurrent=\"%s\">\n",
                  attribute.getType(), attrGuidCache.getGuid(attribute.getId()),
                  attribute.getModificationType().name(), attribute.txCurrent));
            if (attribute.getStringValue().length() > 0) {
               writer.write("<StringValue>");
               Xml.writeAsCdata(writer, attribute.getStringValue());
               writer.write("</StringValue>\n");
            }
            if (attribute.isUriValid()) {
               String relativePath = writeBinaryDataTo(rootDirectory, attribute.getUri());
               writer.write("<BinaryData location=\"");
               writer.write(relativePath);
               writer.write("\" />\n");
            }
            writer.write("</Attribute>\n");
         }
      }
      writer.write("</Artifact>\n");
   }

   private String writeBinaryDataTo(File rootDirectory, String uriTarget) throws Exception {
      String toReturn = null;
      FileOutputStream outputStream = null;
      try {
         int index = uriTarget.lastIndexOf("/");
         String fileName = uriTarget.substring(index + 1, uriTarget.length());
         File target = new File(rootDirectory, fileName);
         outputStream = new FileOutputStream(target);
         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("uri", uriTarget);
         String url =
               HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeApplicationServerContext.RESOURCE_CONTEXT,
                     parameters);
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

   private String getTypeName(ArtifactData artifact) {
      String typeName = typeNameMap.get(artifact.getType());
      if (typeName == null) {
         return artifact.getType();
      }
      return typeName;
   }

   private String getOrderGuid(int orderArtId, boolean isSideA) throws SQLException, IOException {
      String message = null;
      if (isSideA) {
         message = "aorder=\"%s\" ";
      } else {
         message = "border=\"%s\" ";
      }
      return orderArtId > -1 ? String.format(message, artGuidCache.getGuid(orderArtId)) : "";
   }

   private void processLink(LinkData link, Writer writer) throws IOException, SQLException {

      writer.write(String.format(
            "<Link type=\"%s\" guid=\"%s\" aguid=\"%s\" bguid=\"%s\" %s %s modType=\"%s\" txCurrent=\"%s\" >",
            link.getType(), relLinkGuidCache.getGuid(link.getId()), link.getAGuid(), link.getBGuid(), getOrderGuid(
                  link.getAOrder(), true), getOrderGuid(link.getBOrder(), false), link.getModificationType().name(),
            link.getTxCurrent()));

      String rationale = link.getRationale();
      if (rationale != null && rationale.length() > 0) {
         writer.write("<Rationale>");
         Xml.writeAsCdata(writer, rationale);
         writer.write("</Rationale>");
      }

      writer.write("</Link>\n");
   }

   private class BranchData {
      private final int branchId;
      private final String name;
      private final int baseParentTransactionId;
      private final Timestamp time;
      private final String associatedArtGuid;
      private final BranchType branchType;

      public BranchData(Branch branch) throws SQLException, IOException {
         this.branchId = branch.getBranchId();
         this.name = branch.getBranchName();
         this.baseParentTransactionId =
               TransactionIdManager.getParentBaseTransactionNumber(branch.getCreationComment());
         this.time = new Timestamp(branch.getCreationDate().getTime());
         this.associatedArtGuid = artGuidCache.getGuid(branch.getAssociatedArtifactId());
         this.branchType = branch.getBranchType();
      }

      /**
       * @param branchId
       * @param name
       * @param baseParentTransactionId
       * @param time
       * @param associatedArtGuid
       * @throws IOException
       * @throws SQLException
       */
      public BranchData(int branchId, String name, int baseParentTransactionId, Timestamp time, int associatedArtId, int branchTypeId) throws SQLException, IOException {
         super();
         this.branchId = branchId;
         this.name = name;
         this.baseParentTransactionId = baseParentTransactionId;
         this.time = time;
         if (associatedArtId > 0) {
            this.associatedArtGuid = artGuidCache.getGuid(associatedArtId);
         } else {
            associatedArtGuid = null;
         }
         this.branchType = BranchType.getBranchType(branchTypeId);
      }

      public int getBaseParentTransactionId() {
         return baseParentTransactionId;
      }

      public int getBranchId() {
         return branchId;
      }

      public String getName() {
         return name;
      }

      public Timestamp getTime() {
         return time;
      }

      public String getAssociatedArtGuid() {
         return associatedArtGuid;
      }

      public BranchType getBranchType() {
         return branchType;
      }
   }

   private class BranchProcessor implements RsetProcessor<BranchData> {

      public BranchData process(ResultSet set) throws SQLException {
         try {
            return new BranchData(set.getInt("branch_id"), set.getString("branch_name"),
                  TransactionIdManager.getParentBaseTransactionNumber(set.getString("osee_comment")),
                  set.getTimestamp("time"), set.getInt("associated_art_id"), set.getInt("branch_type"));
         } catch (IOException ex) {
            throw new IllegalStateException(ex);
         }
      }

      public boolean validate(BranchData item) {
         return item != null;
      }
   }

   private class TransactionData {
      private final int transactionId;
      private String authorGuid;
      private final Timestamp time;
      private final String comment;
      private String commitArtGuid;
      private Integer txType;
      private final RSetHelper artSet;
      private final RSetHelper attrSet;
      private final RSetHelper linkSet;

      /**
       * @param transactionId
       * @param authorGuid
       * @param time
       * @param comment
       * @throws SQLException
       */
      public TransactionData(int transactionId, RSetHelper artSet, RSetHelper attrSet, RSetHelper linkSet) throws OseeCoreException, SQLException {

         ResultSet linedUpSet;
         if (artSet.onTransaction(transactionId)) {
            linedUpSet = artSet.getRset();
         } else if (attrSet.onTransaction(transactionId)) {
            linedUpSet = attrSet.getRset();
         } else if (linkSet.onTransaction(transactionId)) {
            linedUpSet = linkSet.getRset();
         } else {
            // Should never happen due to the way transactionId is determined
            throw new IllegalStateException("Transaction does not line up to any of the feeding queries");
         }
         try {
            this.authorGuid = SkynetAuthentication.getUserByArtId(linedUpSet.getInt("author")).getGuid();
         } catch (UserNotInDatabase ex) {
            this.authorGuid = "";
         }

         this.transactionId = transactionId;

         this.time = linedUpSet.getTimestamp("time");

         String comment = linedUpSet.getString("osee_comment");
         this.comment = comment == null ? "" : comment;

         try {
            this.commitArtGuid = artGuidCache.getGuid(linedUpSet.getInt("commit_art_id"));
         } catch (Exception ex) {
         }

         if (this.commitArtGuid == null) {
            this.commitArtGuid = "";
         }
         String txTypeString = linedUpSet.getString("tx_type");
         this.txType = txTypeString != null ? new Integer(txTypeString) : null;
         this.artSet = artSet;
         this.attrSet = attrSet;
         this.linkSet = linkSet;
      }

      public String getAuthorGuid() {
         return authorGuid;
      }

      public String getComment() {
         return comment;
      }

      public Timestamp getTime() {
         return time;
      }

      public int getTransactionId() {
         return transactionId;
      }

      public String getCommitArtGuid() {
         return commitArtGuid;
      }

      public Integer getTxType() {
         return txType;
      }

      public RSetHelper getArtSet() {
         return artSet;
      }

      public RSetHelper getAttrSet() {
         return attrSet;
      }

      public RSetHelper getLinkSet() {
         return linkSet;
      }
   }

   private static class ArtifactData {
      private final int artId;
      private final String guid;
      private final String type;
      private final String hrid;
      private final ModificationType modificationType;
      private final int txCurrent;

      /**
       * @param artId
       * @param guid
       * @param type
       * @param hrid
       * @param deleted
       */
      public ArtifactData(final int artId, final String guid, final String type, final String hrid, final ModificationType modificationType, final int txCurrent) {
         this.artId = artId;
         this.guid = guid;
         this.type = type;
         this.hrid = hrid;
         this.modificationType = modificationType;
         this.txCurrent = txCurrent;
      }

      public int getTxCurrent() {
         return txCurrent;
      }

      public int getArtId() {
         return artId;
      }

      public ModificationType getModificationType() {
         return modificationType;
      }

      public String getGuid() {
         return guid;
      }

      public String getHrid() {
         return hrid;
      }

      public String getType() {
         return type;
      }
   }

   private static class ArtifactProcessor implements RsetProcessor<ArtifactData> {

      public ArtifactData process(ResultSet set) throws SQLException {
         return new ArtifactData(set.getInt("art_id"), set.getString("guid"), set.getString("name"),
               set.getString("human_readable_id"), ModificationType.getMod(set.getInt("modification_id")),
               set.getInt("tx_current"));
      }

      public boolean validate(ArtifactData item) {
         return item != null;
      }
   }

   private class AttributeData {
      private final int artId;
      private final String type;
      private final int id;
      private final String stringValue;
      private final String uri;
      private final ModificationType modificationType;
      private final int txCurrent;

      /**
       * @param type
       * @param id
       * @param value
       * @param deleted
       * @throws UnsupportedEncodingException
       */
      public AttributeData(final int artId, final String type, final int id, String stringValue, String uri, final ModificationType modificationType, final int txCurrent) throws UnsupportedEncodingException {
         this.artId = artId;
         this.type = type;
         this.id = id;
         this.modificationType = modificationType;
         this.txCurrent = txCurrent;

         if (stringValue == null) {
            stringValue = "";
         }
         this.stringValue = stringValue;
         this.uri = uri;
      }

      public int getArtId() {
         return artId;
      }

      public int getTxCurrent() {
         return txCurrent;
      }

      public ModificationType getModificationType() {
         return modificationType;
      }

      public int getId() {
         return id;
      }

      public String getType() {
         return type;
      }

      public String getStringValue() {
         return stringValue;
      }

      public String getUri() {
         return uri;
      }

      public boolean isUriValid() {
         return uri != null && uri.length() > 0;
      }
   }

   private class AttributeProcessor implements RsetProcessor<AttributeData> {

      public AttributeData process(ResultSet set) throws SQLException {
         try {
            return new AttributeData(set.getInt("art_id"), set.getString("name"), set.getInt("attr_id"),
                  set.getString("value"), set.getString("uri"), ModificationType.getMod(set.getInt("modification_id")),
                  set.getInt("tx_current"));
         } catch (UnsupportedEncodingException ex) {
            // Don't expect to ever not have UTF-8 support
            throw new IllegalStateException(ex);
         }
      }

      public boolean validate(AttributeData item) {
         return item != null;
      }
   }

   private static class LinkData {
      private final String type;
      private final int id;
      private final String aGuid;
      private final String bGuid;
      private final int aOrder;
      private final int bOrder;
      private final String rationale;
      private final ModificationType modificationType;
      private final int txCurrent;

      /**
       * @param type
       * @param id
       * @param guid
       * @param buid
       * @param order
       * @param order2
       * @param rationale
       */
      public LinkData(final String type, final int id, final String aGuid, final String bGuid, final int aOrder, final int bOrder, final String rationale, final ModificationType modificationType, final int txCurrent) {
         this.type = type;
         this.id = id;
         this.aGuid = aGuid;
         this.bGuid = bGuid;
         this.aOrder = aOrder;
         this.bOrder = bOrder;
         this.rationale = rationale == null ? "" : rationale;
         this.modificationType = modificationType;
         this.txCurrent = txCurrent;
      }

      public String getAGuid() {
         return aGuid;
      }

      public int getTxCurrent() {
         return txCurrent;
      }

      public int getAOrder() {
         return aOrder;
      }

      public String getBGuid() {
         return bGuid;
      }

      public int getBOrder() {
         return bOrder;
      }

      public int getId() {
         return id;
      }

      public String getRationale() {
         return rationale;
      }

      public String getType() {
         return type;
      }

      public ModificationType getModificationType() {
         return modificationType;
      }
   }

   private static class LinkProcessor implements RsetProcessor<LinkData> {

      public LinkData process(ResultSet set) throws SQLException {
         return new LinkData(set.getString("type_name"), set.getInt("rel_link_id"), set.getString("a_guid"),
               set.getString("b_guid"), set.getInt("a_order"), set.getInt("b_order"), set.getString("rationale"),
               ModificationType.getMod(set.getInt("modification_id")), set.getInt("tx_current"));
      }

      public boolean validate(LinkData item) {
         return item != null;
      }
   }

   private static class RSetHelper {
      private final ResultSet rset;
      private boolean empty;

      /**
       * Keeps an 'empty' boolean to state when the end of the result set has been found. This will increment the cursor
       * once to initialize the 'empty' state variable.
       * 
       * @param rset
       * @throws SQLException
       */
      public RSetHelper(final ResultSet rset) throws SQLException {
         this.rset = rset;
         this.empty = !rset.next();
      }

      /**
       * @param transactionId
       * @throws SQLException
       */
      public boolean onTransaction(int transactionId) throws SQLException {
         return !empty && rset.getInt("transaction_id") == transactionId;
      }

      public boolean next() throws SQLException {
         if (empty) return false;
         empty = !rset.next();
         return !empty;
      }

      /**
       * @return the empty
       */
      public boolean isEmpty() {
         return empty;
      }

      /**
       * @return the rset
       */
      public ResultSet getRset() {
         return rset;
      }
   }

   public void mapExportedTypeName(String currentTypeName, String newTypeName) {
      typeNameMap.put(currentTypeName, newTypeName);
   }

   //   public static void main(String[] args) {
   //      new BASE64Encoder().encode("Problem found in action &quot;AAABBWiB_l4B8fugY+_kHg&quot; in state &quot;Identify&quot;\nwhile on page &quot;Action&quot; of OSEE Action version 1.1.13\nProject: General\nTools impacted:OSEE Action  ".getBytes());
   //   }
}
