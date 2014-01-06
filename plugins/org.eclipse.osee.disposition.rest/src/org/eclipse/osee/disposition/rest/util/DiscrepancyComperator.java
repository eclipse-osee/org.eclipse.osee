/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.util;

import java.util.Comparator;
import org.eclipse.osee.disposition.model.Discrepancy;
/**
 * @author Angel Avila
 */
public class DiscrepancyComperator implements Comparator<Discrepancy> {

   private final LocationRangeComparator rangeComp = new LocationRangeComparator();

   @Override
   public int compare(Discrepancy o1, Discrepancy o2) {
      return rangeComp.compare(o1.getLocationRange(), o2.getLocationRange());
   }
}
