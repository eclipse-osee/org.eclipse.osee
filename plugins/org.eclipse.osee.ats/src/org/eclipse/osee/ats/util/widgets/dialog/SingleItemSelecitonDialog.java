/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Angel Avila
 */
public class SingleItemSelecitonDialog extends org.eclipse.ui.dialogs.ListDialog {

   public SingleItemSelecitonDialog(String title, String message) {
      super(Displays.getActiveShell());
      this.setTitle(title);
      this.setMessage(message);
      this.setContentProvider(new ArrayContentProvider() {
         @SuppressWarnings({"rawtypes", "unchecked"})
         @Override
         public Object[] getElements(Object inputElement) {
            if (inputElement instanceof Collection) {
               Collection list = (Collection) inputElement;
               return list.toArray(new Branch[list.size()]);
            }
            return super.getElements(inputElement);
         }
      });
      setLabelProvider(new LabelProvider() {
         @Override
         public String getText(Object element) {
            if (element instanceof Branch) {
               return ((Branch) element).getName();
            }
            return "Unknown element type";
         }
      });
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTableViewer().setSorter(new ArtifactNameSorter());
      return c;
   }

}
