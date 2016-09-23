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
package org.eclipse.osee.orcs.core.internal.relation.sorter;

import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.RelationSorter;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.jdk.core.type.Identifiable;
import org.eclipse.osee.orcs.utility.NameComparator;
import org.eclipse.osee.orcs.utility.SortOrder;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class LexicographicalSorter implements Sorter {

   private final NameComparator comparator;
   private final RelationSorter id;

   public LexicographicalSorter(SortOrder sortOrder) {
      this.comparator = new NameComparator(sortOrder);
      if (sortOrder.isAscending()) {
         id = RelationOrderBaseTypes.LEXICOGRAPHICAL_ASC;
      } else {
         id = RelationOrderBaseTypes.LEXICOGRAPHICAL_DESC;
      }
   }

   @Override
   public RelationSorter getId() {
      return id;
   }

   @Override
   public void sort(List<? extends Identifiable<String>> relatives, List<String> relativeSequence) {
      Collections.sort(relatives, comparator);
   }
}
