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

import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.RelationSorter;

/**
 * @author Andrew M. Finkbeiner
 */
public class UserDefinedSorter implements Sorter {

   @Override
   public RelationSorter getId() {
      return USER_DEFINED;
   }

   @Override
   public void sort(List<? extends ArtifactToken> relatives, List<String> relativeSequence) {
      if (!relatives.isEmpty()) {
         Collections.sort(relatives, new UserDefinedComparator(relativeSequence));
      }
   }
}
