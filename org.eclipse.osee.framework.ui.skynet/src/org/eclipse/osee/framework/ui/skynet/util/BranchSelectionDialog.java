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
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.Collection;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.branch.BranchListComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * @author Donald G. Dunne
 */
public class BranchSelectionDialog extends MessageDialog {

   Branch selected = null;
   BranchListComposite branchListComposite;

   public BranchSelectionDialog(String title) {
      super(Display.getCurrent().getActiveShell(), title, null, null, MessageDialog.NONE,
            new String[] {"Ok", "Cancel"}, 0);
      setShellStyle(getShellStyle() | SWT.RESIZE);
   }

   public Branch getSelection() {
      return selected;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      branchListComposite = new BranchListComposite(container);
      branchListComposite.setPresentation(true);
      GridData gd = new GridData();
      gd.heightHint = 500;
      gd.widthHint = 400;
      branchListComposite.getBranchTable().getTree().setLayoutData(gd);
      branchListComposite.getFilterText().setFocus();
      branchListComposite.getBranchTable().getTree().addListener(SWT.MouseDoubleClick, new Listener() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
          */
         @Override
         public void handleEvent(Event event) {
            handleDoubleClick();
         }
      });
      branchListComposite.getBranchTable().getTree().addSelectionListener(new SelectionListener() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            storeSelectedBranch();
         }

         @Override
         public void widgetDefaultSelected(SelectionEvent e) {
         }
      });
      return branchListComposite.getBranchTable().getControl();
   }

   public Branch getSelected() {
      return selected;
   }

   private void storeSelectedBranch() {
      IStructuredSelection sel = (IStructuredSelection) branchListComposite.getBranchTable().getSelection();
      if (!sel.isEmpty() && (sel.getFirstElement() instanceof JobbedNode)) {
         selected = (Branch) ((JobbedNode) sel.getFirstElement()).getBackingData();
      }
   }

   private void handleDoubleClick() {
      storeSelectedBranch();
      okPressed();
   }

   @Override
   protected void okPressed() {
      if (selected == null) {
         AWorkbench.popup("ERROR", "Must make selection.");
         return;
      }
      super.okPressed();
   }

   public class BranchLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         Branch type = (Branch) arg0;
         return type.getBranchName();
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }

   }

   public void setSelected(Branch selected) {
      this.selected = selected;
   }

   private class BranchContentProvider implements ITreeContentProvider {

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
       */
      public Object[] getChildren(Object parentElement) {
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
       */
      public Object getParent(Object element) {
         return null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
       */
      public boolean hasChildren(Object element) {
         return false;
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
       */
      public Object[] getElements(Object inputElement) {
         return ((Collection<?>) inputElement).toArray();
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IContentProvider#dispose()
       */
      public void dispose() {
      }

      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
       */
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }

   }
}
