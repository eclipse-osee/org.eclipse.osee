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
package org.eclipse.osee.framework.skynet.core.relation;

import java.util.Comparator;

/**
 * Order links in ascending order based on their 'order' value.
 * 
 * @author Robert A. Fisher
 */
public class LinkOrderComparator implements Comparator<RelationLink> {
   boolean sideA;

   /**
    * @param sideA The side to use as the perspective to order from.
    */
   public LinkOrderComparator(boolean sideA) {
      this.sideA = sideA;
   }

   public int compare(RelationLink link1, RelationLink link2) {
      float val;
      if (sideA)
         val = link1.getAOrder() - link2.getAOrder();
      else
         val = link1.getBOrder() - link2.getBOrder();
      // TreeSet's like to remove stuff if zero is returned ... so don't do that if items differ
      if (val == 0 && link1 != link2)
         return link1.getGammaId() - link2.getGammaId();
      else if (link1 == link2)
         return 0;
      else
         return val > 0 ? 1 : -1;
   }
}
