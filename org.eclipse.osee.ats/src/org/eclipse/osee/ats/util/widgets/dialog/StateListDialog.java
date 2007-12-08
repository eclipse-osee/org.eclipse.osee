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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.skynet.util.StringLabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class StateListDialog extends ListDialog {

   public StateListDialog(String title, String message, Collection<String> values) {
      super(Display.getCurrent().getActiveShell());
      setInput(values);
      setTitle(title);
      setMessage(message);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new StringLabelProvider());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.dialogs.ListDialog#createDialogArea(org.eclipse.swt.widgets.Composite)
    */
   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);
      getTableViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare((String) e1, (String) e2);
         }
      });
      return control;
   }

   public String getSelectedState() {
      return (String) getResult()[0];
   }
}
