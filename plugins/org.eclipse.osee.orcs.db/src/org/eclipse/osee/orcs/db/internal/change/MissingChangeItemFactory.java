/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.change;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;

/**
 * @author John Misinco
 */
public interface MissingChangeItemFactory {
   Collection<ChangeItem> createMissingChanges(List<ChangeItem> changes, TransactionToken sourceTx, TransactionToken destTx, ApplicabilityQuery applicQuery);
}