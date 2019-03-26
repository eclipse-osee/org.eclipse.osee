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

import javax.script.ScriptEngine;
import org.eclipse.osee.framework.core.data.IUserGroupService;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.QueryIndexer;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Andrew M. Finkbeiner
 * @author Roberto E. Escobar
 */
public interface OrcsApi {

   QueryIndexer getQueryIndexer();

   QueryFactory getQueryFactory();

   OrcsBranch getBranchOps();

   KeyValueOps getKeyValueOps();

   OrcsAdmin getAdminOps();

   TransactionFactory getTransactionFactory();

   OrcsPerformance getOrcsPerformance();

   OrcsTypes getOrcsTypes();

   ScriptEngine getScriptEngine();

   SystemPreferences getSystemPreferences();

   OrcsApplicability getApplicabilityOps();

   IUserGroupService getUserGroupService();
}