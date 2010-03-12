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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;

/**
 * @author Andrew M. Finkbeiner
 */
class UserDefinedOrderComparator implements Comparator<IArtifact> {

   private final Map<String, Integer> value;

   UserDefinedOrderComparator(List<String> guidOrder) {
      value = new HashMap<String, Integer>(guidOrder.size());
      for (int i = 0; i < guidOrder.size(); i++) {
         value.put(guidOrder.get(i), i);
      }
   }

   @Override
   public int compare(IArtifact artifact1, IArtifact artifact2) {
      Integer val1 = value.get(artifact1.getGuid());
      Integer val2 = value.get(artifact2.getGuid());
      if (val1 == null) {
         val1 = Integer.MAX_VALUE - 1;
      }
      if (val2 == null) {
         val2 = Integer.MAX_VALUE;
      }
      return val1 - val2;
   }
}
