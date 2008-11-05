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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;

/**
 * @author Ryan D. Brooks
 */
public class ConnectWorkflowToTransaction extends AbstractBlam {
   private static final String SELECT_COMMIT_TRANSACTIONS =
         "SELECT * FROM osee_tx_details where osee_comment like ? and commit_art_id is null";
   private static final Pattern hridPattern = Pattern.compile("Commit Branch ([A-Z0-9]{5})[ _]");

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException {
      monitor.subTask("Aquiring Team Workflows");

      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(200, SELECT_COMMIT_TRANSACTIONS, "Commit Branch%");
         while (chStmt.next()) {
            if (monitor.isCanceled()) return;
            updateWorkflow(chStmt.getString("osee_comment"), chStmt.getInt("transaction_id"));
         }
      } finally {
         chStmt.close();
      }
   }

   private void updateWorkflow(String commitComment, int transactionId) throws OseeCoreException {
      Branch atsBranch = BranchManager.getCommonBranch();
      Matcher hridMatcher = hridPattern.matcher(commitComment);

      if (hridMatcher.find()) {
         String hrid = hridMatcher.group(1);

         int artId = ArtifactQuery.getArtifactFromId(hrid, atsBranch).getArtId();
         ConnectionHandler.runPreparedUpdate("UPDATE osee_tx_details SET commit_art_id = ? where transaction_id = ?",
               artId, transactionId);
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