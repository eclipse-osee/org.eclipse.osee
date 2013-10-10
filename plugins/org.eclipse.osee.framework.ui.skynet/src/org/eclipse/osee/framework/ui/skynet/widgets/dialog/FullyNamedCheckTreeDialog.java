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

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.ui.skynet.util.NamedLabelProvider;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class FullyNamedCheckTreeDialog extends CheckedTreeSelectionDialog {

   protected Collection<? extends FullyNamed> initialSel;

   public FullyNamedCheckTreeDialog(Collection<? extends FullyNamed> artifacts) {
      this(artifacts, new NamedLabelProvider());
   }

   public FullyNamedCheckTreeDialog(Collection<? extends FullyNamed> artifacts, ILabelProvider iLabelProvider) {
      super(Displays.getActiveShell(), iLabelProvider, new FullyNamedTreeContentProvider());
      if (artifacts != null) {
         setInput(artifacts);
      }
   }

   public FullyNamedCheckTreeDialog() {
      this(null);
   }

   public Collection<FullyNamed> getSelection() {
      ArrayList<FullyNamed> arts = new ArrayList<FullyNamed>();
      for (Object obj : getResult()) {
         arts.add((FullyNamed) obj);
      }
      return arts;
   }

   public void setInitialSelections(Collection<? extends FullyNamed> initialSel) {
      this.initialSel = initialSel;
      ArrayList<Object> objs = new ArrayList<Object>();
      for (FullyNamed sel : initialSel) {
         objs.add(sel);
      }
      super.setInitialSelections(objs.toArray(new Object[objs.size()]));
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTreeViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare(((FullyNamed) e1).getName(), ((FullyNamed) e2).getName());
         }
      });
      return c;
   }

   public void setArtifacts(Collection<? extends FullyNamed> artifacts) {
      setInput(artifacts);
   }

}
