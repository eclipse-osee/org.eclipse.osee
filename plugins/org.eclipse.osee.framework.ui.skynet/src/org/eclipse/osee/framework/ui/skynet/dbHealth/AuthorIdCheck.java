/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.dbHealth;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Donald G. Dunne
 */
public class AuthorIdCheck extends DatabaseHealthOperation {

   private static final String GET_AUTHOR_IDS = "select distinct (author) from osee_tx_details";
   private static final String UPDATE_AUTHOR_IDS = "update osee_tx_details set author=? where author=?";

   public AuthorIdCheck() {
      super("Author Id Check");
   }

   @Override
   protected void doHealthCheck(IProgressMonitor monitor) throws Exception {
      monitor.subTask("Querying for AuthorIds");
      displayReport(monitor);
      checkForCancelledStatus(monitor);
      monitor.worked(calculateWork(0.50));
      getSummary().append(String.format("Completed"));
      monitor.worked(calculateWork(0.10));
   }

   @SuppressWarnings("FormatString")
   private void displayReport(IProgressMonitor monitor) throws Exception {
      XResultData rd = new XResultData();
      try {
         String[] columnHeaders = new String[] {"Item", "Author Id", "Results"};
         rd.log("Errors show in red.");
         rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));

         Set<ArtifactId> authors = new HashSet<>();
         JdbcStatement chStmt1 = ConnectionHandler.getStatement();
         try {
            chStmt1.runPreparedQuery(GET_AUTHOR_IDS);
            while (chStmt1.next()) {
               checkForCancelledStatus(monitor);
               authors.add(ArtifactId.valueOf(chStmt1.getLong("author")));
            }
         } finally {
            chStmt1.close();
         }
         int num = 0;
         StringBuffer infoSb = new StringBuffer(500);
         for (ArtifactId author : authors) {
            System.out.println(String.format("Processing [%d] %d/%d...", author, num++, authors.size()));
            if (author.isInvalid()) {
               rd.addRaw(AHTML.addRowMultiColumnTable("TX_DETAILS", String.valueOf(author),
                  "Warning: Skipping author < 1; this is ok, but may want to change in future"));
               continue;
            }
            try {
               Artifact artifact = ArtifactQuery.getArtifactOrNull(author, COMMON, DeletionFlag.INCLUDE_DELETED);
               if (artifact == null) {
                  rd.addRaw(
                     AHTML.addRowMultiColumnTable("TX_DETAILS", String.valueOf(author), "Error: Artifact Not Found"));
                  if (isFixOperationEnabled()) {
                     rd.addRaw("Fix needed here");
                  }
               } else if (artifact.isDeleted()) {
                  rd.addRaw(AHTML.addRowMultiColumnTable("TX_DETAILS", String.valueOf(author),
                     "Error: Artifact marked as deleted"));
                  if (isFixOperationEnabled()) {
                     rd.addRaw("Fix needed here");
                  }
               } else {
                  infoSb.append(String.format("Successfully found author [%s] as [%s]\n", String.valueOf(author),
                     artifact.getName()));
               }
            } catch (Exception ex) {
               rd.addRaw(AHTML.addRowMultiColumnTable("TX_DETAILS", String.valueOf(author),
                  "Error: " + ex.getLocalizedMessage()));
               if (isFixOperationEnabled() && ex.getLocalizedMessage().contains("No artifact found with id")) {
                  updateTxAuthor(author);
                  rd.addRaw(String.format("Fix: Updated author [%s] to OSEE System", author));
               }
            }
         }
         rd.addRaw(AHTML.endMultiColumnTable());
         rd.addRaw(infoSb.toString());
         getSummary().append("Processed " + authors.size() + " author ids\n");

      } finally {
         XResultDataUI.report(rd, getName());
      }
   }

   private static void updateTxAuthor(ArtifactId author) throws Exception {
      ConnectionHandler.runPreparedUpdate(UPDATE_AUTHOR_IDS, SystemUser.OseeSystem, author);
   }

   @Override
   public String getCheckDescription() {
      return "Verifies that all author art ids match an un-deleted artifact on Common branch (usually a User artifact)";
   }

   @Override
   public String getFixDescription() {
      return "Sets all invalid authors to the \"OSEE System\" user artifact's art_id.";
   }
}