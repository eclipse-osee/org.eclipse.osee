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
package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.UserCommunity;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class UserCommunityListDialog extends org.eclipse.ui.dialogs.ListDialog {

   public UserCommunityListDialog() {
      super(Display.getCurrent().getActiveShell());
      this.setTitle("Select User Community");
      this.setMessage("Select User Community");
      this.setContentProvider(new ArrayContentProvider() {
         @SuppressWarnings("unchecked")
         @Override
         public Object[] getElements(Object inputElement) {
            if (inputElement instanceof Collection) {
               Collection list = (Collection) inputElement;
               return (list.toArray(new String[list.size()]));
            }
            return super.getElements(inputElement);
         }
      });
      setLabelProvider(new LabelProvider() {
         @Override
         public String getText(Object element) {
            if (element instanceof String) {
               return ((String) element);
            }
            return "Unknown element type";
         }

      });
      setInput(UserCommunity.getInstance().getUserCommunityNames());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTableViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         public int compare(Viewer viewer, Object o1, Object o2) {
            return getComparator().compare((String) o1, (String) o2);
         }
      });
      return c;
   }
}
