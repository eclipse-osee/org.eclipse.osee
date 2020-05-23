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

package org.eclipse.osee.ats.api.util;

import java.util.Comparator;
import org.eclipse.osee.ats.api.user.AtsUser;

/**
 * @author Donald G. Dunne
 */
public class AtsUserNameComparator implements Comparator<AtsUser> {
   private boolean descending = false;

   public AtsUserNameComparator() {
   }

   public AtsUserNameComparator(boolean descending) {
      this.descending = descending;
   }

   @Override
   public int compare(AtsUser user1, AtsUser user2) {
      String name1 = user1.getName();
      String name2 = user2.getName();

      if (descending) {
         return name2.compareTo(name1);
      } else {
         return name1.compareTo(name2);
      }
   }
}
