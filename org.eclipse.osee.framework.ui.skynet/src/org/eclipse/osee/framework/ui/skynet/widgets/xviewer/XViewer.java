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

package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import java.util.Collection;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.ColumnFilterDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.CustomizeManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.customize.FilterDataUI;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Donald G. Dunne
 */
public class XViewer extends TreeViewer {

   public static final String MENU_GROUP_PRE = "XVIEWER MENU GROUP PRE";
   public static final String MENU_GROUP_POST = "XVIEWER MENU GROUP POST";
   private Label statusLabel;
   private final MenuManager menuManager;
   private static boolean ctrlKeyDown = false;
   private static boolean altKeyDown = false;
   protected final IXViewerFactory xViewerFactory;
   private final FilterDataUI filterDataUI;
   private final ColumnFilterDataUI columnFilterDataUI;
   private static boolean ctrlKeyListenersSet = false;

   /**
    * @return the columnFilterDataUI
    */
   public ColumnFilterDataUI getColumnFilterDataUI() {
      return columnFilterDataUI;
   }
   private boolean columnMultiEditEnabled = false;
   private CustomizeManager customizeMgr;
   private TreeColumn rightClickSelectedColumn = null;
   private Integer rightClickSelectedColumnNum = null;
   private TreeItem rightClickSelectedItem = null;

   public XViewer(Composite parent, int style, IXViewerFactory xViewerFactory) {
      super(parent, style);
      this.xViewerFactory = xViewerFactory;
      this.menuManager = new MenuManager();
      this.menuManager.setRemoveAllWhenShown(true);
      this.menuManager.createContextMenu(parent);
      this.filterDataUI = new FilterDataUI(this);
      this.columnFilterDataUI = new ColumnFilterDataUI(this);
      try {
         customizeMgr = new CustomizeManager(this, xViewerFactory);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      createSupportWidgets(parent);

      Tree tree = getTree();
      tree.setHeaderVisible(true);
      tree.setLinesVisible(true);
      setUseHashlookup(true);
      setupCtrlKeyListener();
   }

   public void dispose() {
      filterDataUI.dispose();
      columnFilterDataUI.dispose();
   }

   @Override
   public void setLabelProvider(IBaseLabelProvider labelProvider) {
      if (!(labelProvider instanceof XViewerLabelProvider) && !(labelProvider instanceof XViewerStyledTextLabelProvider)) {
         throw new IllegalArgumentException("Label Provider must extend XViewerLabelProvider");
      }
      super.setLabelProvider(labelProvider);
   }

   public void addCustomizeToViewToolbar(final ViewPart viewPart) {
      addCustomizeToViewToolbar(viewPart.getViewSite().getActionBars().getToolBarManager());
   }

   public Action getCustomizeAction() {
      Action customizeAction = new Action("Customize Table") {

         @Override
         public void run() {
            customizeMgr.handleTableCustomization();
         }
      };
      customizeAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("customize.gif"));
      customizeAction.setToolTipText("Customize Table");
      return customizeAction;
   }

   public void addCustomizeToViewToolbar(IToolBarManager toolbarManager) {
      toolbarManager.add(getCustomizeAction());
   }

   protected void createSupportWidgets(Composite parent) {

      Composite comp = new Composite(parent, SWT.NONE);
      comp.setLayout(ALayout.getZeroMarginLayout(4, false));
      comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      filterDataUI.createWidgets(comp);

      statusLabel = new Label(comp, SWT.NONE);
      statusLabel.setText(" ");
      statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

      getTree().addListener(SWT.MouseDown, new Listener() {
         public void handleEvent(Event event) {
            if (event.button == 3) {
               rightClickSelectedColumn = null;
               rightClickSelectedColumnNum = null;
               rightClickSelectedItem = null;
               Point pt = new Point(event.x, event.y);
               rightClickSelectedItem = getTree().getItem(pt);
               if (rightClickSelectedItem == null) return;
               for (int colNum = 0; colNum < getTree().getColumnCount(); colNum++) {
                  Rectangle rect = rightClickSelectedItem.getBounds(colNum);
                  if (rect.contains(pt)) {
                     rightClickSelectedColumnNum = colNum;
                     rightClickSelectedColumn = getTree().getColumn(colNum);
                     break;
                  }
               }
            }
         }
      });
      getTree().addListener(SWT.MouseUp, new Listener() {
         public void handleEvent(Event event) {
            Point pt = new Point(event.x, event.y);
            TreeItem item = getTree().getItem(pt);
            if (item == null) return;
            for (int colNum = 0; colNum < getTree().getColumnCount(); colNum++) {
               Rectangle rect = item.getBounds(colNum);
               if (rect.contains(pt)) {
                  if (event.button == 1 && ((event.stateMask & SWT.MODIFIER_MASK) == SWT.ALT)) {
                     // System.out.println("Column " + colNum);
                     handleAltLeftClick(getTree().getColumns()[colNum], item);
                  } else if (event.x <= (rect.x + 18)) {
                     handleLeftClickInIconArea(getTree().getColumns()[colNum], item);
                  }
                  // System.out.println("Column " + colNum);
                  handleLeftClick(getTree().getColumns()[colNum], item);
               }
            }
            updateStatusLabel();
         }
      });

      getTree().setMenu(getMenuManager().getMenu());
      columnFilterDataUI.createWidgets(comp);

      customizeMgr.loadCustomization();
   }

   public int getCurrentColumnWidth(XViewerColumn xCol) {
      for (TreeColumn col : getTree().getColumns()) {
         if (col.getText().equals(xCol.getName())) {
            return col.getWidth();
         }
      }
      return 0;
   }

   @Override
   protected void inputChanged(Object input, Object oldInput) {
      super.inputChanged(input, oldInput);
      updateStatusLabel();
   }

   /**
    * Will be called when Alt-Left-Click is done within table cell
    * 
    * @param treeColumn
    * @param treeItem
    * @return true if handled
    */
   public boolean handleAltLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      return false;
   }

   /**
    * Will be called when click is within the first 18 pixels of the cell rectangle where the icon would be. This method
    * will be called in addition to handleLeftClick since both are true.
    * 
    * @param treeColumn
    * @param treeItem
    * @return true if handled
    */
   public boolean handleLeftClickInIconArea(TreeColumn treeColumn, TreeItem treeItem) {
      return false;
   }

   /**
    * Will be called when a cell obtains a mouse left-click. This method will be called in addition to
    * handleLeftClickInIconArea if both are true
    * 
    * @param treeColumn
    * @param treeItem
    * @return true if handled
    */
   public boolean handleLeftClick(TreeColumn treeColumn, TreeItem treeItem) {
      return false;
   }

   public void handleColumnMultiEdit(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
   }

   public boolean isColumnMultiEditable(TreeColumn treeColumn, Collection<TreeItem> treeItems) {
      if (!isColumnMultiEditEnabled()) return false;
      if (!(treeColumn.getData() instanceof XViewerColumn)) return false;
      return (!((XViewerColumn) treeColumn.getData()).isMultiColumnEditable());
   }

   public XViewerColumn getXTreeColumn(int columnIndex) {
      return (XViewerColumn) getTree().getColumn(columnIndex).getData();
   }

   // Only create one listener for all XViewers
   private void setupCtrlKeyListener() {
      if (ctrlKeyListenersSet == false) {
         ctrlKeyListenersSet = true;
         Display.getCurrent().addFilter(SWT.KeyDown, displayKeysListener);
         Display.getCurrent().addFilter(SWT.KeyUp, displayKeysListener);
      }
   }
   Listener displayKeysListener = new Listener() {
      public void handleEvent(org.eclipse.swt.widgets.Event event) {
         if (event.keyCode == SWT.CTRL) {
            if (event.type == SWT.KeyDown) {
               ctrlKeyDown = true;
            } else if (event.type == SWT.KeyUp) {
               ctrlKeyDown = false;
            }
         }
         if (event.keyCode == SWT.ALT) {
            if (event.type == SWT.KeyDown) {
               altKeyDown = true;
            } else if (event.type == SWT.KeyUp) {
               altKeyDown = false;
            }
         }
      }
   };

   public void resetDefaultSorter() {
      customizeMgr.resetDefaultSorter();
   }

   /**
    * Override this method if need to perform other tasks upon remove
    * 
    * @param objects
    */
   public void remove(Collection<Object> objects) {
      super.remove(objects);
   }

   public void load(Collection<Object> objects) {
      super.setInput(objects);
   }

   @Override
   public void setSorter(ViewerSorter sorter) {
      super.setSorter(sorter);
      updateStatusLabel();
   }

   public MenuManager getMenuManager() {
      return this.menuManager;
   }

   public int getVisibleItemCount(TreeItem items[]) {
      int cnt = items.length;
      for (TreeItem item : items)
         if (item.getExpanded()) cnt += getVisibleItemCount(item.getItems());
      return cnt;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.StructuredViewer#refresh()
    */
   @Override
   public void refresh() {
      if (getTree() == null || getTree().isDisposed()) return;
      super.refresh();
      updateStatusLabel();
   }

   public boolean isFiltered() {
      return getFilters().length > 0;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.StructuredViewer#refresh(boolean)
    */
   @Override
   public void refresh(boolean updateLabels) {
      super.refresh(updateLabels);
      updateStatusLabel();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.StructuredViewer#refresh(java.lang.Object, boolean)
    */
   @Override
   public void refresh(Object element, boolean updateLabels) {
      super.refresh(element, updateLabels);
      updateStatusLabel();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.StructuredViewer#refresh(java.lang.Object)
    */
   @Override
   public void refresh(Object element) {
      super.refresh(element);
      updateStatusLabel();
   }

   /**
    * Override this to add information to the status string. eg. extra filters etc.
    * 
    * @return string to add
    */
   public String getStatusString() {
      return "";
   }

   public void getStatusLine1(StringBuffer sb) {
      int loadedNum = 0;
      if (getRoot() != null && ((ITreeContentProvider) getContentProvider()) != null) {
         loadedNum = ((ITreeContentProvider) getContentProvider()).getChildren(getRoot()).length;
      }
      sb.append(" " + loadedNum + " Loaded - " + getVisibleItemCount(getTree().getItems()) + " Shown - " + ((IStructuredSelection) getSelection()).size() + " Selected - ");
      customizeMgr.getStatusLabelAddition(sb);
      filterDataUI.getStatusLabelAddition(sb);
      columnFilterDataUI.getStatusLabelAddition(sb);
      sb.append(getStatusString());
   }

   public void getStatusLine2(StringBuffer sb) {
      customizeMgr.getSortingStr(sb);
   }

   public void updateStatusLabel() {
      if (getTree().isDisposed() || statusLabel.isDisposed()) return;
      StringBuffer sb = new StringBuffer();
      getStatusLine1(sb);
      if (sb.length() > 0) {
         sb.append("\n");
      }
      getStatusLine2(sb);
      String str = sb.toString();
      statusLabel.setText(str);
      statusLabel.getParent().getParent().layout();
      statusLabel.setToolTipText(str);
   }

   public String getViewerNamespace() {
      return getXViewerFactory().getNamespace();
   }

   public IXViewerFactory getXViewerFactory() {
      return xViewerFactory;
   }

   public Label getStatusLabel() {
      return statusLabel;
   }

   /**
    * @return the textFilterComp
    */
   public FilterDataUI getFilterDataUI() {
      return filterDataUI;
   }

   public boolean isColumnMultiEditEnabled() {
      return columnMultiEditEnabled;
   }

   public void setColumnMultiEditEnabled(boolean columnMultiEditEnabled) {
      this.columnMultiEditEnabled = columnMultiEditEnabled;
   }

   /**
    * @return the rightClickSelectedColumn
    */
   public TreeColumn getRightClickSelectedColumn() {
      return rightClickSelectedColumn;
   }

   /**
    * @return the rightClickSelectedItem
    */
   public TreeItem getRightClickSelectedItem() {
      return rightClickSelectedItem;
   }

   public Integer getRightClickSelectedColumnNum() {
      return rightClickSelectedColumnNum;
   }

   public CustomizeManager getCustomizeMgr() {
      return customizeMgr;
   }

   public boolean isCtrlKeyDown() {
      return ctrlKeyDown;
   }

   public boolean isAltKeyDown() {
      return altKeyDown;
   }

}
