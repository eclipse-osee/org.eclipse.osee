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
package org.eclipse.osee.orcs;

import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public interface OrcsApi {

   QueryIndexer getQueryIndexer(ApplicationContext context);

   QueryFactory getQueryFactory(ApplicationContext context);

   OrcsBranch getBranchOps(ApplicationContext context);

   OrcsAdmin getAdminOps(ApplicationContext context);

   TransactionFactory getTransactionFactory(ApplicationContext context);

   OrcsPerformance getOrcsPerformance(ApplicationContext context);

   OrcsTypes getOrcsTypes(ApplicationContext context);

   // TODO remove this call
   BranchCache getBranchCache();

   // TODO remove this call
   TransactionCache getTxsCache();
}
