package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * Identifies and removes addressing from the transaction table that no longer addresses other tables.
 * 
 * @author Jeff C. Phillips
 *
 */
public class CleanUpAddressingData extends DatabaseHealthTask {

	private static final String NOT_ADDRESSESED_GAMMAS = "select gamma_id from osee_Define_txs minus (select gamma_id from osee_Define_artifact_version union select gamma_id from osee_Define_attribute union select gamma_id from osee_Define_rel_link)";
	private static final String NOT_ADDRESSESED_TRANSACTIONS = "select transaction_id from osee_Define_tx_details minus select transaction_id from osee_Define_txs";
	
	@Override
	public String getFixTaskName() {
		return null;
	}

	@Override
	public String getVerifyTaskName() {
		return "Check for not addressed txs data";
	}

	@Override
	public void run(BlamVariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {

		if(operation == Operation.Verify){
			runVerify(builder, showDetails);
		}
		if(operation == Operation.Fix){
		}
	}
	
	private void runVerify(StringBuilder builder, boolean showDetails) throws Exception {
		ConnectionHandlerStatement chStmt = null;
		boolean verifyGammasFailed = false;
		boolean verifyTransactionsFailed = false;
		
		if(showDetails){
			builder.append("Not Addressed Gamma IDs \n");
		}
		
		try{
			chStmt = ConnectionHandler.runPreparedQuery(NOT_ADDRESSESED_GAMMAS);
			while(chStmt.next()){
				verifyGammasFailed = true;
				
				if(showDetails){
					int gammaId = chStmt.getRset().getInt(1);
					builder.append("Gamma ID: " +gammaId + "\n");
				}
			}
			
			if(!showDetails){
				builder.append(verifyGammasFailed? "Failed: Not Addressed Gamma IDs \n" : "Passed: Not Addressed Gamma IDs \n");
			}
		}finally{
			DbUtil.close(chStmt);
		}
		
		if(showDetails){
			builder.append("Not Addressed Transaction IDs \n");
		}
		try{
			chStmt = ConnectionHandler.runPreparedQuery(NOT_ADDRESSESED_TRANSACTIONS);
			while(chStmt.next()){
				verifyTransactionsFailed = true;
				
				if(showDetails){
					int gammaId = chStmt.getRset().getInt(1);
					builder.append("Transaction ID: " +gammaId + "\n");
				}
			}
			if(!showDetails){
				builder.append(verifyTransactionsFailed? "Failed: Not Addressed Transaction IDs \n" : "Passed: Not Addressed Transaction IDs \n");
			}
		}finally{
			DbUtil.close(chStmt);
		}
	}
}
