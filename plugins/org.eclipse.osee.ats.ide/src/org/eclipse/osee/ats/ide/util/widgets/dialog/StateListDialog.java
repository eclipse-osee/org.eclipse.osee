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

package org.eclipse.osee.ats.ide.util.widgets.dialog;

import java.util.Collection;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.ListDialog;

/**
 * @author Donald G. Dunne
 */
public class StateListDialog extends ListDialog {

   public StateListDialog(String title, String message, Collection<String> values) {
      super(Displays.getActiveShell());
      setInput(values);
      setTitle(title);
      setMessage(message);
      setContentProvider(new ArrayContentProvider());
      setLabelProvider(new StringLabelProvider());
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control control = super.createDialogArea(container);
      getTableViewer().setComparator(new ViewerComparator());
      return control;
   }

   public String getSelectedState() {
      if (getResult().length == 0) {
         return "";
      }
      return (String) getResult()[0];
   }
}
