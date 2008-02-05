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

import java.sql.SQLException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.JobbedNode;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.branch.BranchContentProvider;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEEFilteredTree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class BranchSelectionDialog extends MessageDialog {

   Branch selected = null;
   private OSEEFilteredTree oseeFilteredTree;
   private static PatternFilter patternFilter = new PatternFilter();

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

      oseeFilteredTree = new OSEEFilteredTree(container, SWT.SINGLE | SWT.BORDER, patternFilter);
      oseeFilteredTree.getViewer().setContentProvider(new BranchContentProvider());
      oseeFilteredTree.setInitialText("");
      oseeFilteredTree.getFilterControl().setFocus();
      oseeFilteredTree.getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
         /* (non-Javadoc)
          * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
          */
         public void selectionChanged(SelectionChangedEvent event) {
            selected = getSelectedBranch();
         }
      });
      oseeFilteredTree.getViewer().setLabelProvider(
            new org.eclipse.osee.framework.ui.skynet.branch.BranchLabelProvider());
      GridData gd = new GridData(GridData.FILL_BOTH);
      gd.heightHint = 500;
      gd.widthHint = 500;
      oseeFilteredTree.getViewer().getTree().setLayoutData(gd);
      oseeFilteredTree.getViewer().getTree().addListener(SWT.MouseDoubleClick, new Listener() {
         public void handleEvent(Event event) {
            if (event.button == 1) handleDoubleClick();
         }
      });
      oseeFilteredTree.getViewer().getTree().addKeyListener(new KeyListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.events.KeyListener#keyPressed(org.eclipse.swt.events.KeyEvent)
          */
         public void keyPressed(KeyEvent e) {
         }

         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.swt.events.KeyListener#keyReleased(org.eclipse.swt.events.KeyEvent)
          */
         public void keyReleased(KeyEvent e) {
            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) handleDoubleClick();
         }
      });
      try {
         oseeFilteredTree.getViewer().setInput(BranchPersistenceManager.getInstance().getBranches());
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      return container;
   }

   private Branch getSelectedBranch() {
      IStructuredSelection sel = (IStructuredSelection) oseeFilteredTree.getViewer().getSelection();
      if (!sel.isEmpty() && (((JobbedNode) sel.getFirstElement()).getBackingData() instanceof Branch)) selected =
            (Branch) ((JobbedNode) sel.getFirstElement()).getBackingData();
      return selected;
   }

   private void handleDoubleClick() {
      getSelectedBranch();
      okPressed();
   }

   @Override
   protected void okPressed() {
      if (oseeFilteredTree.getViewer().getSelection().isEmpty()) {
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

}
