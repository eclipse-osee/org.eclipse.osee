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
package org.eclipse.osee.ats.ide.workdef.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionCheckTreeDialog extends FilteredCheckboxTreeDialog {

   public WorkDefinitionCheckTreeDialog(String title, String message, List<WorkDefinitionSheet> sheets) {
      super(title, message, new ArrayTreeContentProvider(), new StringLabelProvider());
      if (sheets != null) {
         setInput(sheets);
      }
   }

   public Collection<WorkDefinitionSheet> getSelection() {
      ArrayList<WorkDefinitionSheet> arts = new ArrayList<>();
      for (Object obj : getResult()) {
         arts.add((WorkDefinitionSheet) obj);
      }
      return arts;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTreeViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare(((WorkDefinitionSheet) e1).getName(), ((WorkDefinitionSheet) e2).getName());
         }
      });
      return c;
   }

}
