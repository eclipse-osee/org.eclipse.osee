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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;

/**
 * Set a AList with the members as the selections
 * 
 * @author Donald G. Dunne
 */
public class XMembersList extends XListViewer {

   public XMembersList() {
      this("MList");
   }

   public XMembersList(String displayLabel) {
      super(displayLabel);
      setLabelProvider(new ArtifactLabelProvider());
      setContentProvider(new ArrayContentProvider());
      setInputArtifacts(UserManager.getUsersSortedByName());
   }

   public String[] getEmails() {
      ArrayList<String> v = new ArrayList<>();
      for (Object obj : this.getSelected()) {
         User u = (User) obj;
         String name = u.getName();
         String email = u.getEmail();
         if (!email.equals("")) {
            v.add(email);
         } else {
            v.add(name);
         }
      }
      return v.toArray(new String[0]);
   }

   public ArrayList<User> getUsers() {
      ArrayList<User> v = new ArrayList<>();

      for (Object obj : this.getSelected()) {
         v.add((User) obj);
      }
      return v;
   }
}