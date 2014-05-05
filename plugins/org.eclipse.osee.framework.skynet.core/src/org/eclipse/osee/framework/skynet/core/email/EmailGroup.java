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
package org.eclipse.osee.framework.skynet.core.email;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.utility.EmailUtil;

/**
 * @author Donald G. Dunne
 */
public class EmailGroup {

   private final String groupName;
   private final ArrayList<String> emails = new ArrayList<String>();

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
         } else if (Strings.isValid(str)) {
            OseeLog.logf(Activator.class, Level.SEVERE, "Invalid Email [%s]", str);
         }
      }
   }
}
