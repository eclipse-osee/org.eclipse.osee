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

import org.eclipse.osee.orcs.OseeDb;

/**
 * @author Roberto E. Escobar
 */
public final class TxAuthorIdsSqlHandler extends MainTableFieldSqlHandler {
   public TxAuthorIdsSqlHandler() {
      super(OseeDb.TX_DETAILS_TABLE, "author", SqlHandlerPriority.TX_AUTHOR);
   }
}