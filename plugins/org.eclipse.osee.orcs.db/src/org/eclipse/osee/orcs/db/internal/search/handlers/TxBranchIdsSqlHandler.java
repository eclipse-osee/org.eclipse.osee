/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.handlers;

import org.eclipse.osee.framework.core.enums.TableEnum;

/**
 * @author Roberto E. Escobar
 */
public final class TxBranchIdsSqlHandler extends MainTableFieldSqlHandler {
   public TxBranchIdsSqlHandler() {
      super(TableEnum.TX_DETAILS_TABLE, "branch_id", SqlHandlerPriority.TX_BRANCH_ID);
   }
}