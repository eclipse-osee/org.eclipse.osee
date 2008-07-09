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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactLoader;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.TxChange;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Ryan D. Brooks
 */
public class UpdateFromParentBranch extends AbstractBlam {
    private static final String DELETE_GAMMAS_FOR_UPDATES ="DELETE FROM osee_Define_txs WHERE (transaction_id, gamma_id) IN (Select tx1.transaction_id, tx1.gamma_id FROM osee_Define_txs tx1, osee_Define_tx_details td1, osee_Define_artifact_version av1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.gamma_id = av1.gamma_id AND av1.art_id = ja1.art_id AND ja1.branch_id = ? AND ja1.query_id = ? UNION Select tx2.transaction_id, tx2.gamma_id FROM osee_Define_txs tx2, osee_Define_tx_details td2, osee_Define_attribute at2, osee_join_artifact ja2 WHERE td2.branch_id = ? AND td2.transaction_id = tx2.transaction_id AND tx2.gamma_id = at2.gamma_id AND at2.art_id = ja2.art_id AND ja2.branch_id = ? AND ja2.query_id = ? UNION Select tx3.transaction_id, tx3.gamma_id FROM osee_Define_txs tx3, osee_Define_tx_details td3, osee_Define_rel_link rl3, osee_join_artifact ja3 WHERE td3.branch_id = ? AND td3.transaction_id = tx3.transaction_id AND tx3.gamma_id = rl3.gamma_id AND (rl3.a_art_id = ja3.art_id OR rl3.b_art_id = ja3.art_id) AND ja3.branch_id = ? AND ja3.query_id = ?)";
    private static final String INSERT_UPDATED_ARTIFACTS = "INSERT INTO osee_Define_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_Define_txs tx1, osee_Define_tx_details td1, osee_Define_artifact_version av1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = "+TxChange.CURRENT.getValue()+" AND tx1.gamma_id = av1.gamma_id AND td1.branch_id = ja1.branch_id AND av1.art_id = ja1.art_id AND ja1.query_id = ?";
    private static final String INSERT_UPDATED_ATTRIBUTES_GAMMAS = "INSERT INTO osee_Define_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_Define_txs tx1, osee_Define_tx_details td1, osee_Define_attribute at1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = "+TxChange.CURRENT.getValue()+" AND tx1.gamma_id = at1.gamma_id AND td1.branch_id = ja1.branch_id AND at1.art_id = ja1.art_id AND ja1.query_id = ?";      
    private static final String INSERT_UPDATED_LINKS_GAMMAS = "INSERT INTO osee_Define_txs (transaction_id, gamma_id, mod_type, tx_current) SELECT ?, tx1.gamma_id, tx1.mod_type, tx1.tx_current FROM osee_Define_txs tx1, osee_Define_tx_details td1, osee_Define_rel_link rl1, osee_join_artifact ja1 WHERE td1.branch_id = ? AND td1.transaction_id = tx1.transaction_id AND tx1.tx_current = "+TxChange.CURRENT.getValue()+" AND tx1.gamma_id = rl1.gamma_id AND td1.branch_id = ja1.branch_id AND (rl1.a_art_id = ja1.art_id OR rl1.b_art_id = ja1.art_id) AND ja1.query_id = ?";   

   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Update From Parent Branch", IProgressMonitor.UNKNOWN);

      List<Artifact> artifacts = variableMap.getArtifacts("Parent Branch Artifacts to update to Child Branch");
      Branch childBranch = variableMap.getBranch("Child Branch Name");
      
      if(childBranch == null || artifacts == null || artifacts.isEmpty()){
    	  return;
      }
      
      Branch parentBranch = childBranch.getParentBranch();
      int baselineTransactionNumber = transactionIdManager.getStartEndPoint(childBranch).getKey().getTransactionNumber();
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();
      List<Object[]> datas = new LinkedList<Object[]>();

      try{
	      // insert into the artifact_join_table
	      for (Artifact artifact : artifacts) {
	         datas.add(new Object[] {SQL3DataType.INTEGER, queryId, SQL3DataType.TIMESTAMP, insertTime,
	               SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER, parentBranch.getBranchId()});
	      }
	      ArtifactLoader.selectArtifacts(datas);
	      
	      int count =
	            ConnectionHandler.runPreparedUpdateReturnCount(DELETE_GAMMAS_FOR_UPDATES, SQL3DataType.INTEGER, childBranch.getBranchId(), SQL3DataType.INTEGER, parentBranch.getBranchId(), SQL3DataType.INTEGER, queryId,
	            		SQL3DataType.INTEGER, childBranch.getBranchId(), SQL3DataType.INTEGER, parentBranch.getBranchId(), SQL3DataType.INTEGER, queryId,
	            		SQL3DataType.INTEGER, childBranch.getBranchId(), SQL3DataType.INTEGER, parentBranch.getBranchId(), SQL3DataType.INTEGER, queryId);
	      OSEELog.logInfo(SkynetGuiPlugin.class, "deleted " + count + " gammas", false);
	
	      count =
	            ConnectionHandler.runPreparedUpdateReturnCount(INSERT_UPDATED_ARTIFACTS, SQL3DataType.INTEGER,
	                  baselineTransactionNumber, SQL3DataType.INTEGER, parentBranch.getBranchId(), SQL3DataType.INTEGER,
	                  queryId);
	      OSEELog.logInfo(SkynetGuiPlugin.class, "inserted " + count + " artifacts", false);
	
	      count =
	            ConnectionHandler.runPreparedUpdateReturnCount(INSERT_UPDATED_ATTRIBUTES_GAMMAS, SQL3DataType.INTEGER,
	                    baselineTransactionNumber, SQL3DataType.INTEGER, parentBranch.getBranchId(), SQL3DataType.INTEGER,
	                    queryId);
	      OSEELog.logInfo(SkynetGuiPlugin.class, "inserted " + count + " attributes", false);
	
	      count =
	            ConnectionHandler.runPreparedUpdateReturnCount(INSERT_UPDATED_LINKS_GAMMAS, SQL3DataType.INTEGER,
	                    baselineTransactionNumber, SQL3DataType.INTEGER, parentBranch.getBranchId(), SQL3DataType.INTEGER,
	                    queryId);
	      OSEELog.logInfo(SkynetGuiPlugin.class, "inserted " + count + " relations", false);
	
	      monitor.done();
      }
      finally{
    	  ArtifactLoader.clearQuery(queryId);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Child Branch Name\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Parent Branch Artifacts to update to Child Branch\" /></xWidgets>";
   }
}