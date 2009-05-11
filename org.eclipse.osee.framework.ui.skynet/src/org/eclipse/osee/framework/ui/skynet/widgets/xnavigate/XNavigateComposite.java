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

package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.util.List;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEEFilteredTree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class XNavigateComposite extends Composite {

   protected Browser browser;
   protected OSEEFilteredTree filteredTree;
   private static PatternFilter patternFilter = new PatternFilter();
   protected final XNavigateViewItems navigateViewItems;
   private List<XNavigateItem> items;
   public static enum TableLoadOption {
      None,
      // Wait for table to be loaded before returning; for test only
      ForcePend,
      //
      ClearLastSearchItem,
      // Don't perform UI check, just search
      NoUI,
      // Don't create fresh copy of search item; for test only
      DontCopySearchItem
   };

   /**
    * @param parent
    * @param style
    */
   public XNavigateComposite(XNavigateViewItems navigateViewItems, Composite parent, int style) {
      super(parent, style);
      this.navigateViewItems = navigateViewItems;

      setLayout(new GridLayout(1, false));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      /*
       * Create a grid layout object so the text and treeviewer are layed out the way I want.
       */
      GridLayout layout = new GridLayout();
      layout.numColumns = 1;
      layout.verticalSpacing = 0;
      layout.marginWidth = 0;
      layout.marginHeight = 0;
      parent.setLayout(layout);
      parent.setLayoutData(new GridData(GridData.FILL_BOTH));
      // parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));

      // if (!ConnectionHandler.isConnected()) {
      // (new Label(parent, SWT.NONE)).setText("DB Connection Unavailable");
      // return;
      // }

      filteredTree = new OSEEFilteredTree(this, SWT.SINGLE | SWT.BORDER, patternFilter);
      filteredTree.getViewer().setContentProvider(new XNavigateContentProvider());
      filteredTree.setInitialText("");
      filteredTree.getViewer().setLabelProvider(new XNavigateLabelProvider());
      GridData gd = new GridData(GridData.FILL_BOTH);
      filteredTree.getViewer().getTree().setLayoutData(gd);
      filteredTree.getViewer().addDoubleClickListener(new IDoubleClickListener(){
         @Override
         public void doubleClick(DoubleClickEvent event) {
            try {
               handleDoubleClick();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      filteredTree.getViewer().getTree().addKeyListener(new KeyListener() {
         public void keyPressed(KeyEvent e) {
         }

         public void keyReleased(KeyEvent e) {
            if (e.keyCode == SWT.CR || e.keyCode == SWT.KEYPAD_CR) {
               try {
                  handleDoubleClick();
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
      // Disable native tree tooltip
      filteredTree.getViewer().getTree().setToolTipText("");
      filteredTree.getViewer().getTree().addListener(SWT.Dispose, tableListener);
      filteredTree.getViewer().getTree().addListener(SWT.KeyDown, tableListener);
      filteredTree.getViewer().getTree().addListener(SWT.MouseMove, tableListener);
      filteredTree.getViewer().getTree().addListener(SWT.MouseHover, tableListener);
      
   }

   // Implement a "fake" tooltip
   final Listener labelListener = new Listener() {
      public void handleEvent(Event event) {
         Label label = (Label) event.widget;
         Shell shell = label.getShell();
         switch (event.type) {
            case SWT.MouseDown:
               Event e = new Event();
               e.item = (TableItem) label.getData("_TABLEITEM");
               // Assuming table is single select, set the selection as if
               // the mouse down event went through to the table
               //               filteredTree.getViewer().getTree().setSelection(new TableItem[] {(TableItem) e.item});
               filteredTree.getViewer().getTree().notifyListeners(SWT.Selection, e);
               shell.dispose();
               filteredTree.getViewer().getTree().setFocus();
               break;
            case SWT.MouseExit:
               shell.dispose();
               break;
         }
      }
   };

   Shell tip = null;
   Label label = null;

   private void disposeTooltip() {
      if (tip == null) return;
      tip.dispose();
      tip = null;
      label = null;
   }
   Listener tableListener = new Listener() {

      public void handleEvent(Event event) {
         switch (event.type) {
            case SWT.Dispose:
            case SWT.KeyDown:
            case SWT.MouseMove: {
               if (tip == null) break;
               disposeTooltip();
               break;
            }
            case SWT.MouseHover: {
               TreeItem item = filteredTree.getViewer().getTree().getItem(new Point(event.x, event.y));
               if (item != null && (item.getData() instanceof XNavigateItem) && ((XNavigateItem) item.getData()).getDescription() != null && !((XNavigateItem) item.getData()).getDescription().equals(
                     "")) {
                  if (tip != null && !tip.isDisposed()) tip.dispose();
                  tip = new Shell(Display.getCurrent().getActiveShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
                  tip.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                  FillLayout layout = new FillLayout();
                  layout.marginWidth = 2;
                  tip.setLayout(layout);
                  label = new Label(tip, SWT.NONE);
                  label.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
                  label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
                  label.setData("_TABLEITEM", item);
                  label.setText(item.getText() + "\n\n" + ((XNavigateItem) item.getData()).getDescription());
                  label.addListener(SWT.MouseExit, labelListener);
                  label.addListener(SWT.MouseDown, labelListener);
                  Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
                  Rectangle rect = item.getBounds(0);
                  Point pt = filteredTree.getViewer().getTree().toDisplay(rect.x, rect.y);
                  tip.setBounds(pt.x, pt.y + 15, size.x, size.y);
                  tip.setVisible(true);
               }
            }
         }
      }
   };

   protected void handleDoubleClick() throws OseeCoreException {
      IStructuredSelection sel = (IStructuredSelection) filteredTree.getViewer().getSelection();
      if (!sel.iterator().hasNext()) return;
      XNavigateItem item = (XNavigateItem) sel.iterator().next();
      handleDoubleClick(item);
   }

   protected void handleDoubleClick(XNavigateItem item, TableLoadOption... tableLoadOptions) throws OseeCoreException {
      disposeTooltip();

      if (item.getChildren().size() > 0) {
         filteredTree.getViewer().setExpandedState(item, true);
      } else {
         try {
            item.run(tableLoadOptions);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void refresh() {
      items = navigateViewItems.getSearchNavigateItems();
      Display.getDefault().asyncExec(new Runnable(){
         public void run(){
            filteredTree.getViewer().setInput(items);
         }
      });
   }   
   /**
    * @return the listViewer
    */
   public FilteredTree getFilteredTree() {
      return filteredTree;
   }

   /**
    * @return the patternFilter
    */
   public PatternFilter getPatternFilter() {
      return patternFilter;
   }

   /**
    * @return the items
    */
   public List<XNavigateItem> getItems() {
      return items;
   }
}
