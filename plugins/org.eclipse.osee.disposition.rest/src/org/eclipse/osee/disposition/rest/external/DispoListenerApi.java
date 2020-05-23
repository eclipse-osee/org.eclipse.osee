/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.disposition.rest.external;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.DispoSet;
import org.eclipse.osee.disposition.model.UpdateSummaryData;

/**
 * @author Angel Avila
 */
public interface DispoListenerApi {

   public List<UpdateSummaryData> onUpdateItemStats(Collection<String> ids, Collection<DispoItem> items, DispoSet set);

   public void onDeleteDispoSet(DispoSet set);
}
