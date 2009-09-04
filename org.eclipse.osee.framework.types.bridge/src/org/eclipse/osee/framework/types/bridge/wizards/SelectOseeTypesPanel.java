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
package org.eclipse.osee.framework.types.bridge.wizards;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.types.bridge.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.panels.AbstractItemSelectPanel;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Roberto E. Escobar
 */
public class SelectOseeTypesPanel extends AbstractItemSelectPanel<List<IFile>> {

   public SelectOseeTypesPanel() {
      super(new WorkbenchLabelProvider(), new ArrayContentProvider());
   }

   @Override
   protected Dialog createSelectDialog(Shell shell, List<IFile> lastSelected) throws OseeCoreException {
      CheckedTreeSelectionDialog dialog =
            new CheckedTreeSelectionDialog(shell, new WorkbenchLabelProvider(), new WorkbenchContentProvider());
      dialog.addFilter(new OseeTypesViewerFilter());
      dialog.setTitle("Select OseeTypes to import");
      dialog.setValidator(new Validator());
      dialog.setInput(ResourcesPlugin.getWorkspace().getRoot());
      if (lastSelected != null) {
         dialog.setInitialElementSelections(lastSelected);
      }
      return dialog;
   }

   @Override
   protected boolean updateFromDialogResult(Dialog dialog) {
      boolean updateRequired = false;
      if (dialog instanceof CheckedTreeSelectionDialog) {
         Object[] results = ((CheckedTreeSelectionDialog) dialog).getResult();
         if (results != null && results.length > 0) {
            List<IFile> selected = new ArrayList<IFile>();
            for (Object object : results) {
               if (object instanceof IFile) {
                  selected.add((IFile) object);
               }
            }
            if (!selected.isEmpty()) {
               setSelected(selected);
               updateRequired = true;
            }
         }
      }
      return updateRequired;
   }

   private final class Validator implements ISelectionStatusValidator {
      @Override
      public IStatus validate(Object[] selection) {
         IStatus status = Status.OK_STATUS;
         boolean found = false;
         if (selection != null) {
            for (Object object : selection) {
               if (object instanceof IFile) {
                  found = true;
                  break;
               }
            }
         }
         if (!found) {
            status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "At least (1) must be selected");
         }
         return status;
      }
   }
}