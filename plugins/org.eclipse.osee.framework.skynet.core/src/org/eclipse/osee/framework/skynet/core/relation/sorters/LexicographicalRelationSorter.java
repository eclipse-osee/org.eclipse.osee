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
package org.eclipse.osee.framework.skynet.core.relation.sorters;

import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_ASC;
import static org.eclipse.osee.framework.core.enums.RelationSorter.LEXICOGRAPHICAL_DESC;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactNameComparator;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorter;

/**
 * @author Andrew M. Finkbeiner
 * @author Ryan Schmitt
 */
public class LexicographicalRelationSorter implements IRelationSorter {

   public static enum SortMode {
      ASCENDING,
      DESCENDING;
   }
   private final ArtifactNameComparator comparator;
   private final RelationSorter id;

   public LexicographicalRelationSorter(SortMode sortMode) {
      boolean isDescending = SortMode.DESCENDING == sortMode;
      this.comparator = new ArtifactNameComparator(isDescending);
      this.id = isDescending ? LEXICOGRAPHICAL_DESC : LEXICOGRAPHICAL_ASC;
   }

   @Override
   public RelationSorter getSorterId() {
      return id;
   }

   @Override
   public void sort(List<? extends ArtifactToken> relatives, List<String> relativeSequence) {
      Collections.sort(relatives, comparator);
   }
}
