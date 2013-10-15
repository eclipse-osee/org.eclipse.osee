/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.handlers;

/**
 * @author Roberto E. Escobar
 */
public enum BranchSqlHandlerPriority {
   BRANCH_ANCESTOR_OF,
   BRANCH_CHILD_OF,
   BRANCH_ID,
   BRANCH_GUID,
   BRANCH_TYPE,
   BRANCH_STATE,
   BRANCH_ARCHIVED,
   BRANCH_NAME,
   ALL_BRANCHES;
}