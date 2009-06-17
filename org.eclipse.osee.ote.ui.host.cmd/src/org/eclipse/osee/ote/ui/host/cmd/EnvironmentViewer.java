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
package org.eclipse.osee.ote.ui.host.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.jdk.core.type.TreeObject;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;


/**
 * @author Roberto E. Escobar
 */
public class EnvironmentViewer extends Composite {

   private static final Image ENVIRONMENT_IMAGE = PlatformUI.getWorkbench().getSharedImages().getImage(
         ISharedImages.IMG_OBJ_FOLDER);

   private static final Image USER_IMAGE = UiPlugin.getInstance().getImage("user.gif");

   private static final Image CONSOLE_IMAGE = UiPlugin.getInstance().getImage("console.gif");

   protected enum ColumnEnum {
      Service(SWT.LEFT), Users;

      private int alignment;

      private ColumnEnum() {
         this.alignment = SWT.CENTER;
      }

      private ColumnEnum(int alignment) {
         this.alignment = alignment;
      }

      public int getAlignment() {
         return alignment;
      }

      public static String[] toArray() {
         ColumnEnum[] enumArray = ColumnEnum.values();
         String[] toReturn = new String[enumArray.length];
         for (int index = 0; index < enumArray.length; index++) {
            toReturn[index] = enumArray[index].toString();
         }
         return toReturn;
      }
   }

   private TreeViewer viewer;

   public EnvironmentViewer(Composite parent, int style) {
      super(parent, style);
      createControl();
   }

   public void createControl() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      createTreeArea(this);
      createColumns();
      attachListeners();
      packColumnData();
   }

   private void createTreeArea(Composite parent) {
      viewer = new TreeViewer(parent, SWT.SINGLE | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
      viewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      ((TreeViewer) viewer).getTree().setHeaderVisible(true);
      ((TreeViewer) viewer).getTree().setLinesVisible(true);
      viewer.setUseHashlookup(true);
      viewer.setColumnProperties(ColumnEnum.toArray());
      viewer.setContentProvider(new TreeContentProvider());
      viewer.setLabelProvider(new TreeLabelProvider());
      viewer.setSorter(new ViewerSorter());
      viewer.setInput(new ArrayList<String>());
      viewer.getControl().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
   }

   private void createColumns() {
      for (ColumnEnum columnEnum : ColumnEnum.values()) {
         TreeColumn column = new TreeColumn(viewer.getTree(), SWT.CENTER, columnEnum.ordinal());
         column.setText(columnEnum.toString());
         column.setWidth(columnEnum.toString().length());
         column.setAlignment(columnEnum.getAlignment());
      }
   }

   public void packColumnData() {
      TreeColumn[] columns = viewer.getTree().getColumns();
      for (TreeColumn column : columns) {
         column.pack();
      }
   }

   private void attachListeners() {
      viewer.addTreeListener(new ITreeViewerListener() {

         public void treeCollapsed(TreeExpansionEvent event) {
            packColumnData();
         }

         public void treeExpanded(TreeExpansionEvent event) {
            packColumnData();
         }

      });
   }

   private class TreeLabelProvider implements ITableLabelProvider, ILabelProvider {

      public Image getImage(Object obj) {
         Image toReturn = null;
         if (obj instanceof UserNode) {
            toReturn = USER_IMAGE;
         }
         else if (obj instanceof ConsoleNode) {
            toReturn = CONSOLE_IMAGE;
         } else if (obj instanceof TreeParent) {
            toReturn = ENVIRONMENT_IMAGE;
         }
         return toReturn;
      }

      public String getText(Object obj) {
         if (obj instanceof ConsoleNode) {
            ((ConsoleNode)obj).getName();
         }
         return obj.toString();
      }

      public Image getColumnImage(Object element, int columnIndex) {
         Image toReturn = null;
         ColumnEnum column = ColumnEnum.values()[columnIndex];
         switch (column) {
            case Service:
               if (element instanceof UserNode) {
                  toReturn = USER_IMAGE;
               }
               else if (element instanceof ConsoleNode) {
                  toReturn = CONSOLE_IMAGE;
               } else if (element instanceof TreeParent) {
                  toReturn = ENVIRONMENT_IMAGE;
               }
               break;
            default:
               break;
         }
         return toReturn;
      }

      public String getColumnText(Object element, int columnIndex) {
         String toReturn = null;
         ColumnEnum column = ColumnEnum.values()[columnIndex];
         switch (column) {
            case Service:
               if (element instanceof ConsoleNode) {
                  toReturn = ((ConsoleNode) element).getName();
               } else if (element instanceof TreeParent) {
                  toReturn = element.toString();
               }
               break;
            case Users:
               if (element instanceof ConsoleNode) {
                  toReturn = ((ConsoleNode)element).getUsers();
               }
               break;
            default:               
               break;
         }
         return toReturn;
      }

      public void addListener(ILabelProviderListener listener) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      public void removeListener(ILabelProviderListener listener) {
      }
   }

   private class TreeContentProvider implements ITreeContentProvider {

      public void dispose() {
      }

      public Object[] getChildren(Object parentElement) {
         if (parentElement != null && parentElement instanceof TreeParent) {
            TreeParent parent = (TreeParent) parentElement;
            if (parent.hasChildren()) {
               return parent.getChildren();
            }
         }
         return new Object[0];
      }

      public Object[] getElements(Object inputElement) {
         if (inputElement != null && inputElement instanceof Collection) {
            Collection<?> elementArray = (Collection<?>) inputElement;
            return elementArray.toArray();
         }
         return new Object[0];
      }

      public Object getParent(Object element) {
         if (element != null && element instanceof TreeObject) {
            TreeObject child = (TreeObject) element;
            return child.getParent();
         }
         return new Object();
      }

      public boolean hasChildren(Object element) {
         if (element instanceof TreeParent) {
            TreeParent parent = (TreeParent) element;
            return parent.hasChildren();
         }
         return false;
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      }
   }

   public boolean setFocus() {
      return this.viewer.getControl().setFocus();
   }

   public StructuredViewer getViewer() {
      return viewer;
   }

   public void setInput(List<TreeParent> input) {
      viewer.setInput(input);
   }

   public void refresh() {
      Display.getDefault().asyncExec(new Runnable() {
         public void run() {
            packColumnData();
            viewer.refresh();
         }
      });
   }

   public void dispose() {
      viewer.getControl().dispose();
      super.dispose();
   }
}
