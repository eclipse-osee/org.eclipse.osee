/*******************************************************************************
 * Copyright (c) 2018 Boeing.
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
import java.util.List;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Donald G. Dunne
 */
class UserDefinedRelationOrderComparator extends AbstractUserDefinedOrderComparator implements Comparator<RelationLink> {

   public UserDefinedRelationOrderComparator(List<String> guidOrder) {
      super(guidOrder);
   }

   @Override
   public int compare(RelationLink link1, RelationLink link2) {
      Integer val1 = value.get(link1.getArtifactB().getGuid());
      Integer val2 = value.get(link2.getArtifactB().getGuid());
      return compareIntegers(val1, val2);
   }
}
