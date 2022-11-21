/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.skynet.core.relation.sorters;

import java.util.Comparator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Audrey Denk
 */
class RelOrderComparator implements Comparator<RelationLink> {

   @Override
   public int compare(RelationLink o1, RelationLink o2) {
      return o1.getRelOrder() - (o2.getRelOrder());
   }
}
