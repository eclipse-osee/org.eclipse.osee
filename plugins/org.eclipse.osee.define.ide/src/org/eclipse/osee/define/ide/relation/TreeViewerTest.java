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
package org.eclipse.osee.define.ide.relation;

import java.io.File;
import java.util.Date;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

public class TreeViewerTest {

   class TreeViewerTestLabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {
      Font font = null;

      @Override
      public String getColumnText(Object element, int columnIndex) {
         if (!(element instanceof File)) {
            return null;
         }
         File file = (File) element;
         switch (columnIndex) {
            case 0:
               return file.getName();
            case 1:
               return "" + new Date(file.lastModified());
            case 2:
               return "" + file.isHidden();
         }
         return null;
      }

      @Override
      public Image getColumnImage(Object element, int columnIndex) {
         if (columnIndex != 0) {
            return null;
         }
         if (!(element instanceof File)) {
            return null;
         }
         File file = (File) element;
         if (file.isDirectory()) {
            return Displays.getSystemImage(SWT.ICON_WARNING);
         }
         return Displays.getSystemImage(SWT.ICON_QUESTION);
      }

      @Override
      public void dispose() {
         if (font != null) {
            font.dispose();
         }
         font = null;
      }

      @Override
      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      @Override
      public void addListener(ILabelProviderListener listener) {
         // do nothing
      }

      @Override
      public void removeListener(ILabelProviderListener listener) {
         // do nothing
      }

      @Override
      public Color getForeground(Object element, int columnIndex) {
         if (columnIndex == 1) {
            return Displays.getSystemColor(SWT.COLOR_RED);
         }
         return null;
      }

      @Override
      public Color getBackground(Object element, int columnIndex) {
         if (columnIndex == 0) {
            File file = (File) element;
            if (file.isDirectory()) {
               return Displays.getSystemColor(SWT.COLOR_CYAN);
            }
            return Displays.getSystemColor(SWT.COLOR_MAGENTA);
         }
         return null;
      }

      @Override
      public Font getFont(Object element, int columnIndex) {
         if (columnIndex == 2) {
            if (font == null) {
               Display display = Display.getCurrent();
               font = new Font(display, "Times", 12, SWT.ITALIC);
            }
            return font;
         }
         return null;
      }
   }

   public static void main(String[] args) {
      final Display display = new Display();
      final Shell shell = new Shell(display);
      shell.setLayout(new FormLayout());

      TreeViewerTestLabelProvider labelProvider1 = new TreeViewerTest().new TreeViewerTestLabelProvider();
      ITreeContentProvider contentProvider = new ITreeContentProvider() {
         @Override
         public Object[] getChildren(Object parentElement) {
            if (!(parentElement instanceof File)) {
               return null;
            }
            File file = (File) parentElement;
            if (file.isDirectory()) {
               return file.listFiles();
            }
            return null;
         }

         @Override
         public Object getParent(Object element) {
            if (!(element instanceof File)) {
               return null;
            }
            File file = (File) element;
            return file.getParentFile();
         }

         @Override
         public boolean hasChildren(Object element) {
            if (!(element instanceof File)) {
               return false;
            }
            File file = (File) element;
            return file.isDirectory();
         }

         @Override
         public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
         }

         @Override
         public void dispose() {
            // do nothing
         }

         @Override
         public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            // do nothing
         }
      };

      ILabelProvider labelProvider2 = new ILabelProvider() {
         @Override
         public void dispose() {
            // do nothing
         }

         @Override
         public boolean isLabelProperty(Object element, String property) {
            return false;
         }

         @Override
         public void addListener(ILabelProviderListener listener) {
            // do nothing
         }

         @Override
         public void removeListener(ILabelProviderListener listener) {
            // do nothing
         }

         @Override
         public Image getImage(Object element) {
            if (!(element instanceof File)) {
               return null;
            }
            File file = (File) element;
            if (file.isDirectory()) {
               return display.getSystemImage(SWT.ICON_WARNING);
            }
            return display.getSystemImage(SWT.ICON_QUESTION);
         }

         @Override
         public String getText(Object element) {
            if (!(element instanceof File)) {
               return null;
            }
            File file = (File) element;
            return file.getName();
         }

      };

      final TreeViewer treeViewer1 = new TreeViewer(shell, SWT.FULL_SELECTION | SWT.CHECK);
      Tree tree1 = treeViewer1.getTree();
      tree1.setHeaderVisible(true);
      TreeColumn column1 = new TreeColumn(tree1, SWT.LEFT);
      column1.setText("Name");
      column1.setImage(display.getSystemImage(SWT.ICON_WORKING));
      column1.setWidth(200);
      TreeColumn column2 = new TreeColumn(tree1, SWT.CENTER);
      column2.setText("Last Modified");
      column2.setImage(display.getSystemImage(SWT.ICON_INFORMATION));
      column2.setWidth(200);
      TreeColumn column3 = new TreeColumn(tree1, SWT.CENTER);
      column3.setText("Hidden");
      column3.setImage(display.getSystemImage(SWT.ICON_INFORMATION));
      column3.setWidth(100);
      treeViewer1.setLabelProvider(labelProvider1);
      treeViewer1.setContentProvider(contentProvider);

      final TreeViewer treeViewer2 = new TreeViewer(shell);
      Tree tree2 = treeViewer2.getTree();
      treeViewer2.setLabelProvider(labelProvider2);
      treeViewer2.setContentProvider(contentProvider);

      Button b = new Button(shell, SWT.PUSH);
      b.setText("choose root folder");
      b.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event e) {
            DirectoryDialog f = new DirectoryDialog(shell, SWT.OPEN);
            String root = f.open();
            if (root != null) {
               File file = new File(root);
               if (file.exists()) {
                  treeViewer1.setInput(file);
                  treeViewer1.refresh();
                  treeViewer2.setInput(file);
                  treeViewer2.refresh();
               }
            }
         }
      });

      FormData data = new FormData();
      data.left = new FormAttachment(0, 10);
      data.top = new FormAttachment(0, 10);
      data.right = new FormAttachment(50, -5);
      data.bottom = new FormAttachment(b, -10);
      tree1.setLayoutData(data);

      data = new FormData();
      data.left = new FormAttachment(50, 5);
      data.top = new FormAttachment(0, 10);
      data.right = new FormAttachment(100, -10);
      data.bottom = new FormAttachment(b, -10);
      tree2.setLayoutData(data);

      data = new FormData();
      data.left = new FormAttachment(0, 10);
      data.bottom = new FormAttachment(100, -10);
      b.setLayoutData(data);

      shell.open();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
      display.dispose();
   }
}