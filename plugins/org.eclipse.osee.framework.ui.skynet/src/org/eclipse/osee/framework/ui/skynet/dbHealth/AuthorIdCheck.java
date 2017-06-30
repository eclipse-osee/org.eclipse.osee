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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.UserManager;
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

   private void displayReport(IProgressMonitor monitor) throws Exception {
      XResultData rd = new XResultData();
      try {
         String[] columnHeaders = new String[] {"Item", "Author Id", "Results"};
         rd.log("Errors show in red.");
         rd.addRaw(AHTML.beginMultiColumnTable(100, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(columnHeaders));

         Set<Integer> authors = new HashSet<>();
         JdbcStatement chStmt1 = ConnectionHandler.getStatement();
         try {
            chStmt1.runPreparedQuery(GET_AUTHOR_IDS);
            while (chStmt1.next()) {
               checkForCancelledStatus(monitor);
               int author = chStmt1.getInt("author");
               authors.add(author);
            }
         } finally {
            chStmt1.close();
         }
         int num = 0;
         StringBuffer infoSb = new StringBuffer(500);
         for (Integer author : authors) {
            System.out.println(String.format("Processing [%d] %d/%d...", author, num++, authors.size()));
            if (author == 0) {
               rd.addRaw(AHTML.addRowMultiColumnTable("TX_DETAILS", String.valueOf(author),
                  "Warning: Skipping author == 0; this is ok, but may want to change in future"));
               continue;
            }
            try {
               Artifact artifact = ArtifactQuery.getArtifactFromId(author, COMMON);
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

   private static void updateTxAuthor(int author) throws Exception {
      ConnectionHandler.runPreparedUpdate(UPDATE_AUTHOR_IDS, UserManager.getUser(SystemUser.OseeSystem).getArtId(),
         author);
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
