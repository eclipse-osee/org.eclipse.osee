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
package org.eclipse.osee.framework.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class EnhancedTabFolder implements ITabFolderItem {
   private final TabFolder folder;
   private TabFolderItem previousItem;

   public EnhancedTabFolder(Composite parent, int style) {
      folder = new TabFolder(parent, style);
      folder.addSelectionListener(new SelectionListener() {

         public void widgetDefaultSelected(SelectionEvent e) {
         }

         public void widgetSelected(SelectionEvent event) {
            final TabFolderItem item = (TabFolderItem) ((TabItem) event.item).getControl();
            if (item == previousItem) return;
            if (previousItem != null) previousItem.OnTabDeselected();
            item.OnTabSelected();
            previousItem = item;
         }

      });

      folder.addTraverseListener(new TraverseListener() {

         public void keyTraversed(TraverseEvent e) {
            switch (e.detail) {
               case SWT.TRAVERSE_TAB_NEXT:
               case SWT.TRAVERSE_ARROW_NEXT:
                  System.out.println("Next tab selected");
                  break;
               case SWT.TRAVERSE_TAB_PREVIOUS:
                  System.out.println("previous tab selected");
            }
         }

      });
   }

   public void setSelection(int index) {
      final int selected = folder.getSelectionIndex();
      if (selected != -1) {
         previousItem = (TabFolderItem) folder.getItem(selected).getControl();
      }
      folder.setSelection(index);
   }

   public TabFolderItem getSelection() {
      final int selected = folder.getSelectionIndex();
      if (selected != -1) {
         return (TabFolderItem) folder.getItem(selected).getControl();
      } else {
         return null;
      }
   }

   public TabFolder getFolder() {
      return folder;
   }

   public void addTab(String tabText, ITabFolderItem tab) {
      TabItem item = new TabItem(folder, SWT.NONE);
      item.setControl((Control) tab);
      item.setText(tabText);
   }

   public void addTab(String tabText, String toolTip, ITabFolderItem tab) {
      TabItem item = new TabItem(folder, SWT.NONE);
      item.setControl((Control) tab);
      item.setText(tabText);
      item.setToolTipText(toolTip);
   }

   public void OnTabDeselected() {
      final int index = folder.getSelectionIndex();
      if (index != -1) {
         ((TabFolderItem) folder.getItem(index).getControl()).OnTabSelected();
      }
   }

   public void OnTabSelected() {
      final int index = folder.getSelectionIndex();
      if (index != -1) {
         ((TabFolderItem) folder.getItem(index).getControl()).OnTabDeselected();
      }
   }
}
