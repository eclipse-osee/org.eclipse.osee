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

package org.eclipse.osee.framework.jdk.core.util;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public class EmailGroup {

   private final String groupName;
   private final ArrayList<String> emails = new ArrayList<>();

   public EmailGroup(String groupName, Collection<String> emails) {
      setEmails(emails);
      this.groupName = groupName;
   }

   public String getGroupName() {
      return groupName;
   }

   public boolean hasEmails() {
      if (emails.isEmpty()) {
         return false;
      }
      for (String str : emails) {
         if (EmailUtil.isEmailValid(str)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return groupName + " (" + Collections.toString("; ", emails) + ")";
   }

   public ArrayList<String> getEmails() {
      return emails;
   }

   public void setEmails(Collection<String> emails) {
      this.emails.clear();
      for (String str : emails) {
         if (EmailUtil.isEmailValid(str)) {
            this.emails.add(str);
         }
      }
   }
}
