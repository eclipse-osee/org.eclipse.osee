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
package org.eclipse.osee.framework.jini.service.core;

public class GroupEntry extends FormmatedEntry {

   private static final long serialVersionUID = 132189087526085874L;
   public String[] group;

   public GroupEntry() {
      group = new String[] {"Public"};
   }

   public String getFormmatedString() {
      String groups = "";
      if (group != null) {
         for (int index = 0; index < group.length; index++) {
            groups += group[index];
            if (index + 1 < group.length) {
               groups += ",";
            }
         }
      } else {
         groups = "NULL";
      }
      return "Group(s): {" + groups + "}";
   }

   public boolean equals(Object other) {
      if (!(other instanceof GroupEntry)) return false;
      return group.equals(((GroupEntry) other).group);
   }
}
