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

package org.eclipse.osee.framework.skynet.core.relation.sorters;

import static org.eclipse.osee.framework.core.enums.RelationSorter.UNORDERED;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;

/**
 * @author Andrew M. Finkbeiner
 */
public class UnorderedRelationSorter implements IRelationSorter {

   @Override
   public RelationSorter getSorterId() {
      return UNORDERED;
   }

   @Override
   public void sort(List<? extends ArtifactToken> relatives, List<String> relativeSequence) {
      // do nothing
   }

   @Override
   public void sortRelations(List<? extends RelationLink> listToOrder, List<String> relativeOrder) {
      // do nothing
   }
}
