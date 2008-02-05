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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;

/**
 * @author Ryan D. Brooks
 */
public class ConnectWorkflowToBranchOrTransaction implements BlamOperation {
   private static final Logger logger =
         ConfigUtil.getConfigFactory().getLogger(ConnectWorkflowToBranchOrTransaction.class);
   private static final String SELECT_COMMIT_TRANSACTIONS =
         "SELECT " + TRANSACTION_DETAIL_TABLE.columns("transaction_id", "osee_comment") + " FROM " + TRANSACTION_DETAIL_TABLE + " WHERE " + TRANSACTION_DETAIL_TABLE.column("osee_comment") + " LIKE ?";
   private static final Pattern hridPattern = Pattern.compile("Commit Branch ([A-Z0-9]{5})");

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

      if (monitor.isCanceled()) return;

      monitor.subTask("Aquiring Team Workflow");
      Collection<Artifact> teamWorkflows =
            artifactManager.getArtifactsFromSubtypeName("Lba B3 Req Team Workflow",
                  BranchPersistenceManager.getInstance().getAtsBranch());

      ConnectionHandlerStatement chStmt = null;
      try {
         chStmt = ConnectionHandler.runPreparedQuery(200, SELECT_COMMIT_TRANSACTIONS, SQL3DataType.VARCHAR, "Commit%");
         ResultSet rSet = chStmt.getRset();
         while (chStmt.next()) {
            if (monitor.isCanceled()) return;
            updateWorkflow(teamWorkflows, rSet);
         }
      } finally {
         DbUtil.close(chStmt);
      }
   }

   private void updateWorkflow(Collection<Artifact> teamWorkflows, ResultSet rSet) throws SQLException {
      String commitComment = rSet.getString("osee_comment");
      Matcher hridMatcher = hridPattern.matcher(commitComment);

      if (hridMatcher.find()) {
         String hrid = hridMatcher.group(1);
         for (Artifact workflow : teamWorkflows) {
            if (workflow.getHumanReadableId().equals(hrid)) {
               workflow.setSoleAttributeValue("ats.Transaction Id", String.valueOf(rSet.getInt("transaction_id")));
               workflow.persistAttributes();
               return;
            }
         }
         logger.log(Level.WARNING, "Could not find a team workflow to with the hrid " + hrid);
      } else {
         logger.log(Level.WARNING, "Commit comment not of expected pattern: " + commitComment);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return emptyXWidgetsXml;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }
}