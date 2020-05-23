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

package org.eclipse.osee.orcs.core.internal.relation.sorter;

import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class LexicographicalSorter implements Sorter {

   private final NamedComparator comparator;
   private final RelationSorter id;

   public LexicographicalSorter(SortOrder sortOrder) {
      this.comparator = new NamedComparator(sortOrder);
      if (sortOrder.isAscending()) {
         id = LEXICOGRAPHICAL_ASC;
      } else {
         id = LEXICOGRAPHICAL_DESC;
      }
   }

   @Override
   public RelationSorter getId() {
      return id;
   }

   @Override
   public void sort(List<? extends ArtifactToken> relatives, List<String> relativeSequence) {
      Collections.sort(relatives, comparator);
   }
}
