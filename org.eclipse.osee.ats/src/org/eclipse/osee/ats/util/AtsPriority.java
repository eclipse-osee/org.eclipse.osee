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
package org.eclipse.osee.ats.util;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;

/**
 * @author Donald G. Dunne
 */
public class AtsPriority {

   public static String PRIORITY_HELP_CONTEXT_ID = "atsPriority";

   public static enum PriorityType {
      None(""), Priority_1("1"), Priority_2("2"), Priority_3("3"), Priority_4("4"), Priority_5("5");

      private final String shortName;

      private PriorityType(String shortName) {
         this.shortName = shortName;
      }

      public String getShortName() {
         return shortName;
      }

      public static String[] getPriorities() {
         String priorities[] = new String[PriorityType.values().length];
         int x = 0;
         for (PriorityType pri : PriorityType.values())
            priorities[x++] = pri.toString();
         return priorities;
      }

      public static String[] getPrioritiesWithoutNone() {
         String priorities[] = new String[PriorityType.values().length - 1];
         int x = 0;
         for (PriorityType pri : PriorityType.values())
            if (pri != PriorityType.None) priorities[x++] = pri.toString();
         return priorities;
      }

      @Override
      public String toString() {
         return shortName;
      }

      public static PriorityType getPriority(String type) throws OseeCoreException {
         if (type == null || type.equals("")) return PriorityType.None;
         for (PriorityType pri : PriorityType.values()) {
            if (type.equals(pri.getShortName())) return pri;
         }
         throw new OseeStateException("Invalid priority => " + type);
      }

   };

   public static void openHelp() {
      AtsPlugin.getInstance().displayHelp(PRIORITY_HELP_CONTEXT_ID);
   }

}
