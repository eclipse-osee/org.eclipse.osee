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

package org.eclipse.osee.orcs;

import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.framework.core.OseeApi;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public interface OrcsApi extends OseeApi {

   QueryIndexer getQueryIndexer();

   QueryFactory getQueryFactory();

   OrcsBranch getBranchOps();

   KeyValueOps getKeyValueOps();

   OrcsAdmin getAdminOps();

   TransactionFactory getTransactionFactory();

   OrcsPerformance getOrcsPerformance();

   OrcsTypes getOrcsTypes();

   SystemProperties getSystemProperties();

   OrcsApplicability getApplicabilityOps();

   JdbcService getJdbcService();

   ActivityLog getActivityLog();

}