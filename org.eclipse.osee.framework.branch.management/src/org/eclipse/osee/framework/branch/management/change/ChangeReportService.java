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
package org.eclipse.osee.framework.branch.management.change;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.branch.management.IChangeReportService;
import org.eclipse.osee.framework.branch.management.internal.InternalBranchActivator;
import org.eclipse.osee.framework.core.cache.TransactionCache;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportRequest;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportService implements IChangeReportService {
	private final IOseeDatabaseServiceProvider provider;
	private final IOseeCachingServiceProvider cachingProvider;

	public ChangeReportService(IOseeDatabaseServiceProvider provider, IOseeCachingServiceProvider cachingProvider) {
		this.provider = provider;
		this.cachingProvider = cachingProvider;
	}

	public void getChanges(IProgressMonitor monitor, ChangeReportRequest request, ChangeReportResponse response)
			throws OseeCoreException {

		TransactionCache txCache = cachingProvider.getOseeCachingService().getTransactionCache();
		TransactionRecord srcTx = txCache.getOrLoad(request.getSourceTx());
		TransactionRecord destTx = txCache.getOrLoad(request.getDestinationTx());

		List<IOperation> ops = new ArrayList<IOperation>();
		if (request.isHistorical()) {
			ops.add(new LoadChangeDataOperation(provider, srcTx.getId(), destTx, response.getChangeItems()));
		} else {
			ops.add(new LoadChangeDataOperation(provider, srcTx, destTx, null, response.getChangeItems()));
		}
		ops.add(new ComputeNetChangeOperation(response.getChangeItems()));

		String opName = String.format("Gathering changes");
		IOperation op = new CompositeOperation(opName, InternalBranchActivator.PLUGIN_ID, ops);
		Operations.executeWorkAndCheckStatus(op, monitor, -1);
	}

}
