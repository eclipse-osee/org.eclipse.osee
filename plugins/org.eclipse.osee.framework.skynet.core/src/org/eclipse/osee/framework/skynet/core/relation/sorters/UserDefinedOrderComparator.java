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

import java.util.Comparator;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Andrew M. Finkbeiner
 */
class UserDefinedOrderComparator extends AbstractUserDefinedOrderComparator implements Comparator<ArtifactToken> {

   public UserDefinedOrderComparator(List<String> guidOrder) {
      super(guidOrder);
   }

   @Override
   public int compare(ArtifactToken artifact1, ArtifactToken artifact2) {
      Integer val1 = value.get(artifact1.getGuid());
      Integer val2 = value.get(artifact2.getGuid());
      return compareIntegers(val1, val2);
   }
}
