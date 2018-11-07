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
package org.eclipse.osee.ats.api.util;

import java.util.Comparator;
import org.eclipse.osee.ats.api.user.IAtsUser;

/**
 * @author Donald G. Dunne
 */
public class AtsUserNameComparator implements Comparator<IAtsUser> {
   private boolean descending = false;

   public AtsUserNameComparator() {
   }

   public AtsUserNameComparator(boolean descending) {
      this.descending = descending;
   }

   @Override
   public int compare(IAtsUser user1, IAtsUser user2) {
      String name1 = user1.getName();
      String name2 = user2.getName();

      if (descending) {
         return name2.compareTo(name1);
      } else {
         return name1.compareTo(name2);
      }
   }
}
