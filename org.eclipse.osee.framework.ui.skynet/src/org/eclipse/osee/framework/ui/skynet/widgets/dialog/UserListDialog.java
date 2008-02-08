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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class UserListDialog extends ListDialog {

   public UserListDialog(Shell parent) {
      this(parent, "Select User");
   }

   public UserListDialog(Shell parent, String title) {
      super(parent);
      setTitle(title);
      setMessage(title);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new LabelProvider() {
         @Override
         public String getText(Object element) {
            if (element instanceof User) {
               return ((User) element).getName();
            }
            return "Unknown Object";
         }
      });
      setInput(SkynetAuthentication.getInstance().getUsers());
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTableViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare(((User) e1).getName(), ((User) e2).getName());
         }
      });
      return c;
   }

   public User getSelection() {
      return (User) getResult()[0];
   }

}
