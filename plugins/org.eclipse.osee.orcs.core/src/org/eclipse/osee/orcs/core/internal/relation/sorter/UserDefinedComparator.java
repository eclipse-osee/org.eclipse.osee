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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Andrew M. Finkbeiner
 */
public class UserDefinedComparator implements Comparator<ArtifactToken> {

   private final Map<String, Integer> value;

   public UserDefinedComparator(List<String> guidOrder) {
      value = new HashMap<>(guidOrder.size());
      for (int i = 0; i < guidOrder.size(); i++) {
         value.put(guidOrder.get(i), i);
      }
   }

   @Override
   public int compare(ArtifactToken object1, ArtifactToken object2) {
      Integer val1 = getIndex(object1);
      Integer val2 = getIndex(object2);
      if (val1 == null) {
         val1 = Integer.MAX_VALUE - 1;
      }
      if (val2 == null) {
         val2 = Integer.MAX_VALUE;
      }
      return val1 - val2;
   }

   private Integer getIndex(ArtifactToken object) {
      Integer toReturn = null;
      if (object != null) {
         toReturn = value.get(object.getGuid());
      }
      return toReturn;
   }

}
