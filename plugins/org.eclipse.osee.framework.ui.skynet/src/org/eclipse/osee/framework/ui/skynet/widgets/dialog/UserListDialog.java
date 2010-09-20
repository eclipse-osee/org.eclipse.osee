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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.ArtifactContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class UserListDialog extends ListDialog {

   public UserListDialog(Shell parent) throws OseeCoreException {
      this(parent, "Select User");
   }

   public UserListDialog(Shell parent, String title) throws OseeCoreException {
      super(parent);
      setTitle(title);
      setMessage(title);
      setContentProvider(new ArtifactContentProvider());
      setLabelProvider(new LabelProvider() {
         @Override
         public String getText(Object element) {
            if (element instanceof User) {
               return ((User) element).getName();
            }
            return "Unknown Object";
         }
      });
      System.err.println("Switch this back to only active: getUsersSortedByName");
      setInput(UserManager.getUsersAllSortedByName());
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public User getSelection() {
      return (User) getResult()[0];
   }
}