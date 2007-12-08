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
package org.eclipse.osee.framework.ui.skynet.util.email;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Donald G. Dunne
 */
public class EmailGroup {

   private final String groupName;
   private ArrayList<String> emails = new ArrayList<String>();

   public EmailGroup(String groupName, Collection<String> emails) {
      this.groupName = groupName;
      for (String s : emails)
         this.emails.add(s);
   }

   public String getGroupName() {
      return groupName;
   }

   public String toString() {
      return groupName;
   }

   public ArrayList<String> getEmails() {
      return emails;
   }

   public void setEmails(ArrayList<String> emails) {
      this.emails = emails;
   }
}
