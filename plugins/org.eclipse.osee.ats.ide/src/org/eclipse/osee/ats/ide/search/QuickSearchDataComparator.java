/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.search;

import java.util.Comparator;
import org.eclipse.osee.ats.api.query.AtsSearchData;

/**
 * @author Donald G. Dunne
 */
public class QuickSearchDataComparator implements Comparator<AtsSearchData> {
   public QuickSearchDataComparator() {
   }

   @Override
   public int compare(AtsSearchData data1, AtsSearchData data2) {
      String name1 = data1.getSearchName();
      String name2 = data2.getSearchName();
      return name1.compareTo(name2);
   }
}
