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
package org.eclipse.osee.ats.operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.database.ConnectionHandler;
import org.eclipse.osee.framework.database.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.DbUtil;
import org.eclipse.osee.framework.database.sql.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;

/**
 * @author Ryan D. Brooks
 */
public class ConnectWorkflowToTransaction extends AbstractBlam {
   private static final String SELECT_COMMIT_TRANSACTIONS =
         "SELECT * FROM osee_define_tx_details where osee_comment like ? and commit_art_id is null";
   private static final Pattern hridPattern = Pattern.compile("Commit Branch ([A-Z0-9]{5})[ _]");

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.subTask("Aquiring Team Workflows");

      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(200, SELECT_COMMIT_TRANSACTIONS, SQL3DataType.VARCHAR,
                     "Commit Branch%");
         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            if (monitor.isCanceled()) return;
            updateWorkflow(rSet.getString("osee_comment"), rSet.getInt("transaction_id"));
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void updateWorkflow(String commitComment, int transactionId) throws SQLException {
      ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
      Branch atsBranch = BranchPersistenceManager.getInstance().getCommonBranch();
      Matcher hridMatcher = hridPattern.matcher(commitComment);

      if (hridMatcher.find()) {
         String hrid = hridMatcher.group(1);
         Collection<Artifact> artiafcts = artifactManager.getArtifactsFromHrid(hrid, atsBranch);
         if (artiafcts.size() == 1) {
            int artId = artiafcts.iterator().next().getArtId();
            ConnectionHandler.runPreparedUpdate(
                  "UPDATE osee_define_tx_details SET commit_art_id = ? where transaction_id = ?", SQL3DataType.INTEGER,
                  artId, SQL3DataType.INTEGER, transactionId);
         } else {
            appendResultLine("expected to find one match for HRID " + hrid + " not " + artiafcts.size());
         }
      } else {
         appendResultLine("Commit comment not of expected pattern: " + commitComment);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return emptyXWidgetsXml;
   }
}