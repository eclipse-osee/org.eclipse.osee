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
import org.eclipse.osee.framework.core.data.IRelationSorterId;
import org.eclipse.osee.framework.core.data.Identifiable;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;

/**
 * @author Andrew M. Finkbeiner
 */
public class UserDefinedSorter implements Sorter {

   @Override
   public IRelationSorterId getId() {
      return RelationOrderBaseTypes.USER_DEFINED;
   }

   @Override
   public void sort(List<? extends Identifiable> relatives, List<String> relativeSequence) {
      if (!relatives.isEmpty()) {
         Collections.sort(relatives, new UserDefinedComparator(relativeSequence));
      }
   }
}
