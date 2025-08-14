/*********************************************************************
 * Copyright (c) 2025 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.orcs.rest.internal.operations;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactRow;
import org.eclipse.osee.framework.core.data.ArtifactRow.ArtifactRowComparator;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeRow;
import org.eclipse.osee.framework.core.data.AttributeRow.AttributeRowComparator;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationLinkRow;
import org.eclipse.osee.framework.core.data.RelationLinkRow.RelationLinkRowComparator;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionDetails;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionRow;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.ElapsedTime;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * Loads artifact's attr, rels, txs, tags from db and reports any anomalies.
 *
 * @author Donald G. Dunne
 */
public class ArtifactValidityReport {

   private static final String ADD_ERRORS_HERE = "ADD_ERRORS_HERE";
   private static TransactionId NewBranchFromCommon = TransactionId.valueOf(150021L);
   private final OrcsApi orcsApi;
   private ArtifactReadable art;
   List<AttributeRow> attributes = new ArrayList<>(200);
   List<ArtifactRow> artifacts = new ArrayList<>(200);
   List<RelationLinkRow> relations = new ArrayList<>(200);
   Set<TransactionId> txIds = new HashSet<>(100);
   Map<TransactionId, TransactionDetails> txIdToDetails = new HashMap<>(100);
   private List<TransactionDetails> txDetails;
   Map<ArtifactId, ArtifactReadable> artIdToArtRead = new HashMap<>(200);
   private final BranchId branchId;
   private BranchToken branch;
   private XResultData rd;
   private XResultData errorRd;
   private final ArtifactId artId;
   HashCollection<GammaId, TransactionId> gammaIdToTxIds = new HashCollection<>(100);
   private int dereferencedAttrsFound = 0;
   private ArtifactRow artRow;

   public ArtifactValidityReport(BranchId branchId, ArtifactId artId, OrcsApi orcsApi) {
      this.branchId = branchId;
      this.artId = artId;
      this.orcsApi = orcsApi;
   }

   public String getReport() {
      rd = new XResultData();
      errorRd = new XResultData();
      rd.logf("<div id=\"top\"><br/><h3>Artifact Validity Report - Run: " + new Date() + "</h3>");
      rd.logf("BranchId: %s<br/>", branchId.getIdString());
      branch = orcsApi.getQueryFactory().branchQuery().andId(branchId).getResults().getAtMostOneOrNull();
      if (branch == null) {
         logError("Error: Branch Not Found");
         return rd.toString();
      }
      rd.logf("Branch: %s<br/>", branch.toStringWithId());
      rd.logf("ArtId: %s<br/>", artId);

      ArtifactToken artTok = orcsApi.getQueryFactory().fromBranch(branchId).andId(artId).getResults().getOneOrNull();
      if (artTok == null) {
         logError("Error: Artifact Not Found");
         return rd.toString();
      }
      art = (ArtifactReadable) artTok;
      rd.logf("Artifact: %s<br/>", art.toStringWithId());
      rd.logf("<b>Note: New Branch from Common Tx: %s</b><br/>", NewBranchFromCommon);

      rd.log("Jump To: <a href=\"#attrrpt\">Attributes Report</a>&nbsp;&nbsp;");
      rd.log("<a href=\"#relrpt\">Relations Report</a>&nbsp;&nbsp;");
      rd.log("<a href=\"#txrpt\">Transactions Report</a><br/><br/>");

      rd.log(ADD_ERRORS_HERE); // Placeholder for errors

      loadArtifacts();
      artifacts.sort(new ArtifactRowComparator());
      if (rd.isErrors()) {
         return rd.toString();
      }

      loadAttributesTxs();
      attributes.sort(new AttributeRowComparator());

      if (rd.isErrors()) {
         return rd.toString();
      }

      loadRelationLinkTxs();
      relations.sort(new RelationLinkRowComparator());
      if (rd.isErrors()) {
         return rd.toString();
      }
      loadRelationLinkArts();
      if (rd.isErrors()) {
         return rd.toString();
      }

      loadTxs();
      txDetails.sort(new TxDetailsComparator());
      if (rd.isErrors()) {
         return rd.toString();
      }

      loadTxDetails();

      findDereferncedAttrs();
      //      findDereferncedRels(); -- TBD

      validateTxCurrents();

      getTxsRowCountForArtifacts(); // takes too long from home

      reportArtifacts();
      reportAttributes();
      reportRelations();
      reportTxs();

      // TBD - Generate fix sql queries

      // un-referenced data
      // referenced missing data
      // missing type
      // check attr data type for value (no str for long, asdf for boolean, etc)
      // duplicate sole attr; multi tx_current = 1
      // duplicate rels
      // missing uri
      // rel missing a or b art
      // missing tx_details
      // tx_details author, etc...

      return rd.toString().replaceFirst(ADD_ERRORS_HERE, errorRd.toString());
   }

   public void loadArtifacts() {
      JdbcStatement chStmt = orcsApi.getJdbcService().getClient().getStatement();
      try {
         chStmt.runPreparedQuery(
            "SELECT * FROM osee_artifact art, OSEE_TXS txs WHERE art.art_id = ? AND txs.BRANCH_ID = ? AND art.GAMMA_ID = txs.GAMMA_ID",
            art.getIdString(), branchId);
         while (chStmt.next()) {
            ArtifactTypeToken artType = orcsApi.tokenService().getArtifactTypeOrCreate(chStmt.getLong("art_type_id"));
            GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
            ArtifactId artId = ArtifactId.valueOf(chStmt.getLong("art_id"));
            TransactionId txId = TransactionId.valueOf(chStmt.getInt("transaction_id"));

            TxCurrent txCurrent = TxCurrent.valueOf(chStmt.getInt("tx_current"));
            ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));

            artRow = new ArtifactRow(branch, gammaId, artId, modType, artType);
            artRow.setTx(txId);
            artRow.setTxCurrent(txCurrent);
            artifacts.add(artRow);
            txIds.add(txId);
         }
         artRow = artifacts.iterator().next();
      } finally {
         chStmt.close();
      }
   }

   private void loadRelationLinkArts() {
      Set<ArtifactId> relArts = new HashSet<>(200);
      for (RelationLinkRow link : relations) {
         relArts.add(link.getaArtId());
         relArts.add(link.getbArtId());
      }

      for (ArtifactReadable art : orcsApi.getQueryFactory().fromBranch(branch).andIds(
         relArts).includeDeletedArtifacts().asArtifacts()) {
         artIdToArtRead.put(art.getArtifactId(), art);
      }

      for (RelationLinkRow link : relations) {
         ArtifactReadable aArt = artIdToArtRead.get(link.getaArtId());
         if (aArt == null) {
            logError("A_ART_ID %s not found for link %s<br/>", link.getaArtId(), link.getRelationId());
         }
         ArtifactReadable bArt = artIdToArtRead.get(link.getbArtId());
         if (bArt == null) {
            logError("B_ART_ID %s not found for link %s<br/>", link.getaArtId(), link.getRelationId());
         }
      }
   }

   public void loadRelationLinkTxs() {
      JdbcStatement chStmt = orcsApi.getJdbcService().getClient().getStatement();
      try {
         chStmt.runPreparedQuery(
            "SELECT * FROM osee_relation_link rel, OSEE_TXS txs WHERE txs.BRANCH_ID = ? AND (rel.a_art_id = ? or rel.b_art_id = ?) AND rel.GAMMA_ID = txs.GAMMA_ID",
            branchId, art.getIdString(), art.getIdString());
         while (chStmt.next()) {
            RelationTypeToken relType =
               orcsApi.tokenService().getRelationTypeOrCreate(chStmt.getLong("rel_link_type_id"));
            GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
            ArtifactId aArtId = ArtifactId.valueOf(chStmt.getLong("a_art_id"));
            ArtifactId bArtId = ArtifactId.valueOf(chStmt.getLong("b_art_id"));
            RelationId relId = RelationId.valueOf(chStmt.getLong("rel_link_id"));
            TransactionId txId = TransactionId.valueOf(chStmt.getInt("transaction_id"));
            TxCurrent txCurrent = TxCurrent.valueOf(chStmt.getInt("tx_current"));
            ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));

            RelationLinkRow relRow = new RelationLinkRow(aArtId, bArtId, branch, relType, relId, gammaId, null, 0,
               bArtId, modType, ApplicabilityId.SENTINEL);
            relRow.setTx(txId);
            relRow.setTxCurrent(txCurrent);
            relations.add(relRow);
            txIds.add(txId);
            gammaIdToTxIds.put(gammaId, txId);
         }
      } finally {
         chStmt.close();
      }
   }

   public void loadAttributesTxs() {
      JdbcStatement chStmt = orcsApi.getJdbcService().getClient().getStatement();
      try {
         chStmt.runPreparedQuery(
            "SELECT * FROM osee_attribute attr, OSEE_TXS txs WHERE attr.art_id = ? AND txs.BRANCH_ID = ? AND attr.GAMMA_ID = txs.GAMMA_ID",
            art.getIdString(), branchId);
         while (chStmt.next()) {
            AttributeTypeToken attributeType =
               orcsApi.tokenService().getAttributeTypeOrCreate(chStmt.getLong("attr_type_id"));
            GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
            ArtifactId artId = ArtifactId.valueOf(chStmt.getLong("art_id"));
            AttributeId attrId = AttributeId.valueOf(chStmt.getLong("attr_id"));
            String value = chStmt.getString("value");
            String uri = chStmt.getString("uri");
            TransactionId txId = TransactionId.valueOf(chStmt.getInt("transaction_id"));

            TxCurrent txCurrent = TxCurrent.valueOf(chStmt.getInt("tx_current"));
            ModificationType modType = ModificationType.valueOf(chStmt.getInt("mod_type"));

            if ((Strings.isInvalid(value) || "null".equals(value)) && Strings.isInvalid(uri)) {
               if (modType.equals(ModificationType.DELETED) && txId.equals(NewBranchFromCommon)) {
                  value = "null value, because deleted before <New Branch From Common> tx: " + NewBranchFromCommon;
               } else {
                  if (value == null || "null".equals(value)) {
                     logError("Error: Unexpected null or \"null\" (and no uri) value for attr_id %s gamma_id %s<br/>",
                        attrId, gammaId);
                  } else if (value.equals("")) {
                     logError("Error: Unexpected \"\" (and no uri) value for attr_id %s gamma_id %s<br/>", attrId,
                        gammaId);
                  }
               }
            }

            AttributeRow attrRow = new AttributeRow(branchId, gammaId, artId, modType, value, attrId, attributeType);
            attrRow.setTx(txId);
            attrRow.setUri(uri);
            attrRow.setTxCurrent(txCurrent);
            attributes.add(attrRow);
            txIds.add(txId);
            gammaIdToTxIds.put(gammaId, txId);
         }
      } finally {
         chStmt.close();
      }
   }

   private void validateTxCurrents() {
      if (artifacts.size() > 1) {
         logError("Artifact Rows: Expected 1; Actual %s<br/>", artifacts.size());
      }
      validateTxCurrents(artifacts, "Art");
      validateTxCurrents(attributes, "Attr");
      validateTxCurrents(relations, "Rel");
   }

   public void validateTxCurrents(Collection<? extends TransactionRow> txRows, String table) {
      TransactionRow lastTxRow = null;
      List<TransactionRow> rowSet = new ArrayList<>();
      for (TransactionRow txRow : txRows) {
         if (lastTxRow != null && !lastTxRow.getItemId().equals(txRow.getItemId())) {
            validateLastTxCurrent(lastTxRow, table);
            checkDuplicateTxCurrents(rowSet, table);
            rowSet.clear();
         }
         lastTxRow = txRow;
         rowSet.add(txRow);
      }
      validateLastTxCurrent(lastTxRow, table);
      checkDuplicateTxCurrents(rowSet, table);
      checkHistoricalTxCurrents(rowSet, table);
   }

   private void checkHistoricalTxCurrents(List<TransactionRow> rowSet, String table) {
      int x = 1;
      int lastRow = rowSet.size();
      for (TransactionRow row : rowSet) {
         if (row.getTxCurrent().isCurrent() && x < lastRow) {
            logError("Tx Historical (%s): Tx is %s of %s rows but current for tx %s<br/>", table, x, lastRow,
               row.getTx());
         }
         x++;
      }
   }

   private void checkDuplicateTxCurrents(List<TransactionRow> rowSet, String table) {
      Integer currents = 0;
      for (TransactionRow row : rowSet) {
         if (!row.isDereferenced() && row.getTxCurrent().isCurrent()) {
            currents++;
         }
      }
      if (currents > 1) {
         logError("Tx Current Count (%s): Expected 1; Was %s for Id: %s<br/>", table, currents,
            rowSet.iterator().next().getItemId());
      }
   }

   private void validateLastTxCurrent(TransactionRow lastTxRow, String table) {
      if (lastTxRow != null && !lastTxRow.isDereferenced()) {
         ModificationType lastModType = lastTxRow.getModType();
         TxCurrent lastTxCurr = lastTxRow.getTxCurrent();
         TxCurrent lastExpectedTxCurrent = TxCurrent.getCurrent(lastModType);
         if (!lastExpectedTxCurrent.equals(lastTxCurr)) {
            logError("TxCurrent Mismatch (%s): expected %s actual %s for gamma %s and tx %s<br/>", table,
               lastExpectedTxCurrent.toStringWithId(), lastTxCurr.toStringWithId(), lastTxRow.getGammaId(),
               lastTxRow.getTx());
         }
      }
   }

   public void reportTxs() {
      rd.logf("<div id=\"txrpt\"></div><h4>Transaction Report&nbsp;&nbsp;<a href=\"#top\">Top</a></h4>");
      rd.log(AHTML.beginMultiColumnTable(98, 2));
      rd.log(AHTML.addHeaderRowMultiColumnTable(
         Arrays.asList("TX_ID", "TIME", "AUTHOR", "TX_COMMENT").toArray(new String[4])));
      boolean greyBg = true;
      for (TransactionDetails txd : txDetails) {
         greyBg = !greyBg;
         boolean notFoundTxId = !txIds.contains(txd.getTxId());
         UserToken user = orcsApi.userService().getUser(txd.getAuthor().getId());
         if (notFoundTxId) {
            logError("Error: Tx Details not found for %s<br/>", txd.getTxId());
         }
         rd.addRaw(AHTML.addRowMultiColumnTableWithBg( //
            (greyBg ? AHTML.LIGHT_GREY_BACKGROUND : AHTML.WHITE_BACKGROUND), //
            "<div id=\"txid" + txd.getTxId().getIdString() + "\"></div>" + txd.getTxId().getIdString(), //
            DateUtil.getMMDDYYHHMM(txd.getTime()), user.getName(), txd.getOseeComment()));
      }
      rd.addRaw(AHTML.endMultiColumnTable());
   }

   public void reportArtifacts() {
      rd.logf(
         "<div id=\"artrpt\"><h4>Artifact(s) Report (should only be one here)&nbsp;&nbsp;<a href=\"#top\">Top</a></h4>");
      rd.log(AHTML.beginMultiColumnTable(98, 2));
      rd.log(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("ART_TYPE", "ART_ID", "GAMMA_ID", "MOD_TYPE", "TX_CURR",
         "TX_ID", "TXS_COUNT", "TX_COMMENT").toArray(new String[8])));
      for (ArtifactRow art : artifacts) {
         String txIdStr = "";
         String oseeComment = "";
         String modTypeStr = "";
         if (art.isDereferenced()) {
            txIdStr = "Dereferenced";
            oseeComment = "N/A";
            modTypeStr = "N/A";
         } else {
            oseeComment = art.getTxd() == null ? "Error: NO TX_DETAILS" : art.getTxd().getOseeComment();
            txIdStr = "<a href=\"#txid" + art.getTx().getIdString() + "\">" + art.getTx().getIdString() + "</a>";
            modTypeStr = art.getModType().getName();
         }
         rd.addRaw(AHTML.addRowMultiColumnTable( //
            art.getArtType().getName(), art.getArtId().getIdString(), art.getGammaId().toString(), modTypeStr,
            art.getTxCurrent().getIdString(), //
            txIdStr, //
            (art.getTxsRowCount() == null ? "not loaded" : String.valueOf(art.getTxsRowCount())), //
            oseeComment));
      }
      rd.addRaw(AHTML.endMultiColumnTable());
   }

   public void reportAttributes() {
      rd.logf("<div id=\"attrrpt\"><h4>Attributes Report&nbsp;&nbsp;<a href=\"#top\">Top</a></h4>");
      rd.log(AHTML.beginMultiColumnTable(98, 2));
      rd.log(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("ATTR_TYPE", "ATTR_ID", "GAMMA_ID", "MOD_TYPE", "TX_CURR",
         "TX", "TX_COMMENT", "VALUE", "URI").toArray(new String[9])));
      boolean greyBg = true;
      AttributeId lastAttrId = AttributeId.SENTINEL;
      for (AttributeRow attr : attributes) {
         if (!lastAttrId.equals(attr.getAttrId())) {
            greyBg = !greyBg;
         }
         String txIdStr = "";
         String oseeComment = "";
         String modTypeStr = "";
         if (attr.isDereferenced()) {
            txIdStr = "Dereferenced";
            oseeComment = "N/A";
            modTypeStr = "N/A";
         } else {
            oseeComment = attr.getTxd() == null ? "Error: NO TX_DETAILS" : attr.getTxd().getOseeComment();
            txIdStr = "<a href=\"#txid" + attr.getTx().getIdString() + "\">" + attr.getTx().getIdString() + "</a>";
            modTypeStr = attr.getModType().getName();
         }
         rd.addRaw(AHTML.addRowMultiColumnTableWithBg( //
            (greyBg ? AHTML.LIGHT_GREY_BACKGROUND : AHTML.WHITE_BACKGROUND), attr.getAttributeType().getName(),
            attr.getAttrId().getIdString(), attr.getGammaId().toString(), modTypeStr, attr.getTxCurrent().getIdString(), //
            txIdStr, //
            oseeComment, AHTML.textToHtml(attr.getValue()), attr.getUri()));
         lastAttrId = attr.getAttrId();
      }
      rd.addRaw(AHTML.endMultiColumnTable());
   }

   public void reportRelations() {
      rd.logf("<div id=\"relrpt\"><h4>Relations Report&nbsp;&nbsp;<a href=\"#top\">Top</a></h4>");
      rd.log(AHTML.beginMultiColumnTable(98, 2));
      rd.log(AHTML.addHeaderRowMultiColumnTable(Arrays.asList("REL_TYPE", "REL_ID", "A_ART_ID", "B_ART_ID", "GAMMA_ID",
         "MOD_TYPE", "TX_CURR", "TX", "TX_COMMENT").toArray(new String[9])));
      RelationId lastRelId = RelationId.SENTINEL;
      boolean greyBg = true;
      for (RelationLinkRow rel : relations) {
         if (!lastRelId.equals(rel.getRelationId())) {
            greyBg = !greyBg;
         }
         String txIdStr = "";
         String oseeComment = "";
         String modTypeStr = "";
         if (rel.isDereferenced()) {
            txIdStr = "Dereferenced";
            oseeComment = "N/A";
            modTypeStr = "N/A";
         } else {
            oseeComment = rel.getTxd() == null ? "Error: NO TX_DETAILS" : rel.getTxd().getOseeComment();
            txIdStr = "<a href=\"#txid" + rel.getTx().getIdString() + "\">" + rel.getTx().getIdString() + "</a>";
            modTypeStr = rel.getModType().getName();
         }
         rd.addRaw(AHTML.addRowMultiColumnTableWithBg( //
            (greyBg ? AHTML.LIGHT_GREY_BACKGROUND : AHTML.WHITE_BACKGROUND), rel.getRelationType().getName(),
            rel.getRelationId().getIdString(), getShortIdName(rel.getaArtId()), getShortIdName(rel.getbArtId()),
            rel.getGammaId().toString(), modTypeStr, rel.getTxCurrent().getIdString(), //
            txIdStr, //
            oseeComment));
         lastRelId = rel.getRelationId();
      }
      rd.addRaw(AHTML.endMultiColumnTable());
   }

   private String getShortIdName(ArtifactId artId) {
      ArtifactToken art = artIdToArtRead.get(artId);
      if (art == null) {
         return artId + "- NO ART FOUND";
      }
      return Strings.truncate(art.getId() + " - [" + art.getName() + "]", 26, true);
   }

   public static final class TxDetailsComparator implements Comparator<TransactionDetails> {

      @Override
      public int compare(TransactionDetails a, TransactionDetails b) {
         // reverse txId compare
         return -a.getTxId().getId().compareTo(b.getTxId().getId());
      }
   }

   public void loadTxs() {
      JdbcStatement chStmt = orcsApi.getJdbcService().getClient().getStatement();
      try {
         List<TransactionId> txIdsList = new ArrayList<>();
         txIdsList.addAll(txIds);
         // Only 1000 allowed in sql statement
         List<Collection<TransactionId>> subDivide = Collections.subDivide(txIdsList, 900);
         for (Collection<TransactionId> txIdsSet : subDivide) {
            String query = String.format("SELECT * FROM osee_tx_details txd WHERE txd.transaction_id in (%s)",
               Collections.toString(",", Id.getIs(txIdsSet)));
            chStmt.runPreparedQuery(query);
            while (chStmt.next()) {
               TransactionId txId = TransactionId.valueOf(chStmt.getLong("transaction_id"));
               String oseeComment = chStmt.getString("osee_comment");
               Date time = null;
               try {
                  time = DateUtil.getDate("yyyyMMddHHmmss", chStmt.getString("time"));
               } catch (ParseException ex) {
                  time = DateUtil.getSentinalDate();
               }
               int txType = chStmt.getInt("tx_type");
               ArtifactId commitArtId = ArtifactId.valueOf(chStmt.getLong("commit_art_id"));
               Long buildId = chStmt.getLong("build_id");
               ArtifactId author = ArtifactId.valueOf(chStmt.getLong("author"));
               TransactionDetails txDetails =
                  new TransactionDetails(txId, branch, time, oseeComment, txType, commitArtId, buildId, author);
               txIdToDetails.put(txId, txDetails);
            }
         }
      } catch (Exception ex) {
         logError("Error: Exception loading txs %s", Lib.exceptionToString(ex));
      } finally {
         chStmt.close();
      }

      txDetails = new ArrayList<>(txIdToDetails.size());
      txDetails.addAll(txIdToDetails.values());
   }

   private void loadTxDetails() {
      loadTxDetails(artifacts, "Art");
      loadTxDetails(attributes, "Attr");
      loadTxDetails(relations, "Rel");
   }

   private void loadTxDetails(Collection<? extends TransactionRow> rows, String table) {
      for (TransactionRow txRow : rows) {
         TransactionDetails txd = txIdToDetails.get(txRow.getTx());
         if (txd == null) {
            logError("Not TX_DETAILS found for %s txId %s<br/>", table, txRow.getTx());
         } else {
            txRow.setTxd(txd);
         }
      }
   }

   public void findDereferncedAttrs() {
      JdbcStatement chStmt = orcsApi.getJdbcService().getClient().getStatement();
      try {
         chStmt.runPreparedQuery("SELECT * FROM osee_attribute attr WHERE attr.art_id = ?", art.getIdString());
         while (chStmt.next()) {
            GammaId gammaId = GammaId.valueOf(chStmt.getLong("gamma_id"));
            if (!gammaIdToTxIds.containsKey(gammaId)) {
               AttributeTypeToken attributeType =
                  orcsApi.tokenService().getAttributeTypeOrCreate(chStmt.getLong("attr_type_id"));
               AttributeId attrId = AttributeId.valueOf(chStmt.getLong("attr_id"));
               ArtifactId artId = ArtifactId.valueOf(chStmt.getLong("art_id"));
               String value = chStmt.getString("value");
               String uri = chStmt.getString("uri");
               AttributeRow attrRow =
                  new AttributeRow(branchId, gammaId, artId, ModificationType.SENTINEL, value, attrId, attributeType);
               attrRow.setUri(uri);
               attrRow.setDereferenced(true);
               attributes.add(attrRow);
               dereferencedAttrsFound++;
            }
         }
      } finally {
         chStmt.close();
      }
      if (dereferencedAttrsFound > 0) {
         logWarning("%s Dereferenced Attribute(s) Found (no txs entry) - Search Dereferenced<br/>",
            dereferencedAttrsFound);
      }
   }

   private void logError(String formatStr, Object... data) {
      errorRd.log(AHTML.color("red", String.format("Error: " + formatStr, data)));
   }

   private void logWarning(String formatStr, Object... data) {
      errorRd.log(AHTML.color("orange", String.format("Warning: " + formatStr, data)));
   }

   private void getTxsRowCountForArtifacts() {
      JdbcStatement chStmt = orcsApi.getJdbcService().getClient().getStatement();
      try {
         Set<Long> txIds = new HashSet<Long>();
         for (ArtifactRow artRow : artifacts) {
            txIds.add(artRow.getTx().getId());
         }

         /**
          * Query for count of txs entries for each transaction_id for this artifact; If there's only one, it is an
          * artifact entry and should NOT be there. These can be automatically fixed by purging the transaction. The
          * others need to be analyzed for removal of just the artifact entry.
          */
         Map<TransactionId, Integer> txIdToTxsCount = new HashMap<>();
         if (!txIds.isEmpty()) {
            String query = String.format("SELECT txs.transaction_id, COUNT(*) AS cnt " + //
               "FROM osee_txs txs " + //
               "WHERE txs.branch_id = ? AND  " + //
               "txs.transaction_id IN (%s) " + //
               "GROUP BY txs.transaction_id ORDER BY txs.transaction_id", Collections.toString(",", txIds)); //
            chStmt.runPreparedQuery(query, branch);
            while (chStmt.next()) {
               txIdToTxsCount.put(TransactionId.valueOf(chStmt.getLong("transaction_id")), chStmt.getInt("cnt"));
            }
         }

         Set<TransactionId> singleEntryTxIdsToPurge = new HashSet<>();
         for (ArtifactRow artRow : artifacts) {
            Integer cnt = txIdToTxsCount.get(artRow.getTx());
            if (cnt != null) {
               artRow.setTxsRowCount(cnt);
               if (cnt == 1) {
                  singleEntryTxIdsToPurge.add(artRow.getTx());
               }
            }
         }
         if (singleEntryTxIdsToPurge.size() > 0) {
            rd.logf("<br/>Single Entry TxIds to Purge Count: %s<br/>", singleEntryTxIdsToPurge.size());
            rd.logf("Single Entry TxIds to Purge: %s<br/>", Collections.toString(",", singleEntryTxIdsToPurge));

            boolean fix = false;
            List<TransactionId> txIdList = new ArrayList<>();
            txIdList.addAll(singleEntryTxIdsToPurge);
            List<Collection<TransactionId>> subDivide = Collections.subDivide(txIdList, 20);
            int x = 1;
            int size = subDivide.size();
            for (Collection<TransactionId> txIdsSet : subDivide) {
               System.err.println(String.format("To Purge %s/%s: %s", x++, size, txIdsSet));
               if (fix) {
                  try {
                     ElapsedTime time = new ElapsedTime("Purging txIds..." + txIdsSet);
                     orcsApi.getTransactionFactory().purgeTransaction(txIdsSet).call();
                     time.endSec();
                  } catch (Exception ex) {
                     System.err.println(Lib.exceptionToString(ex));
                  }
               }
            }
         }

      } finally {
         chStmt.close();
      }
   }

}