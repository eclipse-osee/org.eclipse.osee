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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEEFilteredTree;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
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
      filteredTree.getViewer().getTree().addListener(SWT.MouseDoubleClick, new Listener() {
         public void handleEvent(Event event) {
            if (event.button == 1) handleDoubleClick();
         }
      });
      filteredTree.getViewer().getTree().addKeyListener(new KeyListener() {
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
   }

   protected void handleDoubleClick() {
      IStructuredSelection sel = (IStructuredSelection) filteredTree.getViewer().getSelection();
      if (!sel.iterator().hasNext()) return;
      XNavigateItem item = (XNavigateItem) sel.iterator().next();

      if (item.getChildren().size() > 0) {
         filteredTree.getViewer().setExpandedState(item, true);
      } else {
         try {
            item.run();
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }

   public void refresh() {
      items = navigateViewItems.getSearchNavigateItems();
      filteredTree.getViewer().setInput(items);
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
