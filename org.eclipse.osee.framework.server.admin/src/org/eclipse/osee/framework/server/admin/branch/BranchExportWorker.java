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
package org.eclipse.osee.framework.server.admin.branch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

/**
 * @author Roberto E. Escobar
 */
public class BranchExportWorker extends BaseCmdWorker {

	private static final String ALL_BRANCHES_QUERY = "select br1.branch_id from osee_define_branch br1%s ORDER BY br1.branch_id";

	private boolean isValidArg(String arg) {
		return arg != null && arg.length() > 0;
	}

	private String getAllBranchesQuery(boolean includeArchivedBranches) {
		return String.format(ALL_BRANCHES_QUERY,
				includeArchivedBranches ? "" : " where br1.archived <> 1"); 
						
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osee.framework.server.admin.BaseCmdWorker#doWork(long)
	 */
	@Override
	protected void doWork(long startTime) throws Exception {
		String arg = null;
		int count = 0;
		String exportFileName = null;
		boolean excludeBaselineTxs = false;
		boolean includeArchivedBranches = false;
		List<Integer> branchIds = new ArrayList<Integer>();
		do {
			arg = getCommandInterpreter().nextArgument();
			if (isValidArg(arg)) {
				if (arg.equals("-excludeBaselineTxs")) {
					excludeBaselineTxs = true;
				} else if (arg.equals("-includeArchivedBranches")) {
					includeArchivedBranches = true;
				} else {
					if (count == 0 && !arg.startsWith("-")) {
						exportFileName = arg;
					} else {
						branchIds.add(new Integer(arg));
					}
				}
			}
			count++;
		} while (isValidArg(arg));

		if (!isValidArg(exportFileName)) {
			throw new IllegalArgumentException(String.format(
					"exportFileName was invalid: [%s]", exportFileName));
		}

		if (branchIds.isEmpty()) {
			ConnectionHandlerStatement chStmt = null;
			try {
				chStmt = ConnectionHandler
						.runPreparedQuery(getAllBranchesQuery(includeArchivedBranches));
				while (chStmt.next()) {
					branchIds.add(chStmt.getRset().getInt("branch_id"));
				}
			} finally {
				DbUtil.close(chStmt);
			}
		}
		println(String.format("Exporting: [%s] branches\n", branchIds.size()));

		Options options = new Options();
		options.put(ExportOptions.EXCLUDE_BASELINE_TXS.name(),
				excludeBaselineTxs);
		Activator.getInstance().getBranchExchange().exportBranch(exportFileName, options, branchIds);
	}
}
