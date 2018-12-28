/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.checkbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.skynet.widgets.checkbox.images.CheckBoxStateImageCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class CheckBoxStateTreeViewerExample {

   private static CheckBoxStateTreeViewer<ExampleTreeNode> treeViewer;
   private static Text textBox;

   public static void main(String[] args) {
      // Get the Display. Create on if none exists
      Display Display_1 = Display.getCurrent();
      boolean displayCreated = false;
      if (Display_1 == null) {
         Display_1 = Display.getDefault();
         displayCreated = true;
      } else {
         Display_1 = Display.getDefault();
      }

      Shell Shell_1 = new Shell(Display_1, SWT.SHELL_TRIM);
      Shell_1.setText("CheckBoxStateTreeViewer Test");
      Shell_1.setBounds(0, 0, 1000, 500);
      Shell_1.setLayout(new GridLayout(2, false));
      Shell_1.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.HORIZONTAL_ALIGN_BEGINNING));

      Composite toolBarComposite = new Composite(Shell_1, SWT.NONE);
      toolBarComposite.setLayout(new GridLayout(2, false));
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 2;
      toolBarComposite.setLayoutData(gd);

      treeViewer = new CheckBoxStateTreeViewer<>(Shell_1, SWT.MULTI | SWT.BORDER);
      treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_VERTICAL));
      treeViewer.setContentProvider(new ExampleCheckBoxStateTreeContentProvider());
      treeViewer.setLabelProvider(new CheckBoxStateTreeLabelProvider(treeViewer));
      treeViewer.addCheckListener(new ICheckBoxStateTreeListener() {
         @Override
         public void checkStateChanged(Object obj) {
            textBox.setText(String.format("State Changed [%s] to [%s]", obj,
               String.valueOf(treeViewer.isChecked(obj)).toUpperCase()));
         }
      });

      textBox = new Text(Shell_1, SWT.BORDER | SWT.WRAP);
      textBox.setLayoutData(new GridData(GridData.FILL_BOTH));

      createTaskActionBar(toolBarComposite);

      List<Object> tasks = new ArrayList<>();
      for (int x = 0; x < 1; x++) {
         tasks.addAll(getTestTasks(treeViewer));
      }
      treeViewer.setInput(tasks);
      treeViewer.expandAll();
      Shell_1.open();
      while (!Shell_1.isDisposed()) {
         if (!Display_1.readAndDispatch()) {
            Display_1.sleep();
         }
      }

      if (displayCreated) {
         Display_1.dispose();
      }
   }

   public static void createTaskActionBar(Composite parent) {

      Composite actionComp = new Composite(parent, SWT.NONE);
      actionComp.setLayout(new GridLayout());
      actionComp.setLayoutData(new GridData(GridData.END));

      ToolBar toolBar = new ToolBar(actionComp, SWT.FLAT | SWT.RIGHT);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      toolBar.setLayoutData(gd);

      ToolItem getChecked = new ToolItem(toolBar, SWT.PUSH);
      getChecked.setImage(CheckBoxStateImageCache.getImage("report.gif"));
      getChecked.setToolTipText("Get Checked");
      getChecked.setText("Checked");
      getChecked.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            List<ExampleTreeNode> checked = treeViewer.getChecked();
            openReport("Checked Items", checked);
         }
      });

      ToolItem deSelectAll = new ToolItem(toolBar, SWT.PUSH);
      deSelectAll.setImage(CheckBoxStateImageCache.getImage("chkbox_unchecked.gif"));
      deSelectAll.setToolTipText("De-Select All");
      deSelectAll.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            List<Object> tasks = new ArrayList<>();
            for (int x = 0; x < 1; x++) {
               tasks.addAll(getTestTasks(treeViewer));
            }
            treeViewer.deSelectAll();
         }
      });

      ToolItem refreshItem = new ToolItem(toolBar, SWT.PUSH);
      refreshItem.setImage(CheckBoxStateImageCache.getImage("refresh.gif"));
      refreshItem.setToolTipText("Refresh");
      refreshItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            List<Object> tasks = new ArrayList<>();
            for (int x = 0; x < 1; x++) {
               tasks.addAll(getTestTasks(treeViewer));
            }
            treeViewer.setInput(tasks);
            treeViewer.expandAll();
         }
      });
   }

   protected static void openReport(String title, Collection<ExampleTreeNode> items) {
      StringBuilder sb = new StringBuilder();
      for (ExampleTreeNode node : items) {
         sb.append(node.getName() + "\n");
      }
      textBox.setText(sb.toString());
   }

   private static List<ExampleTreeNode> getTestTasks(ICheckBoxStateTreeViewer treeViewer) {
      List<ExampleTreeNode> tasks = new ArrayList<>();
      ExampleTreeNode task = new ExampleTreeNode("Trees", null, true, treeViewer);
      tasks.add(task);
      for (String str : Arrays.asList("Pine", "Redwood", "Oak")) {
         new ExampleTreeNode(str + " will run to test this test", task, true, treeViewer);
      }

      ExampleTreeNode node = new ExampleTreeNode("Rocks", null, false, treeViewer);
      treeViewer.setEnabled(node, false);
      tasks.add(node);

      ExampleTreeNode task2 = new ExampleTreeNode("Animals", null, false, treeViewer);
      tasks.add(task2);
      for (String str : Arrays.asList("Dog", "Cat", "Squirrel", "Lizard", "Bird")) {
         new ExampleTreeNode(str + " will run to test this test", task2, !str.contains("Cat"), treeViewer);
      }
      return tasks;
   }

   private static class ExampleCheckBoxStateTreeContentProvider implements ITreeContentProvider {

      @Override
      public void dispose() {
         // TODO Auto-generated method stub

      }

      @Override
      public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
         // TODO Auto-generated method stub

      }

      @SuppressWarnings("unchecked")
      @Override
      public Object[] getElements(Object inputElement) {
         if (inputElement instanceof List<?>) {
            return ((List<ExampleTreeNode>) inputElement).toArray(new ExampleTreeNode[((List<?>) inputElement).size()]);
         }
         if (inputElement instanceof ExampleTreeNode) {
            return ((ExampleTreeNode) inputElement).getChildren().toArray();
         }
         return null;
      }

      @Override
      public Object[] getChildren(Object parentElement) {
         return ((ExampleTreeNode) parentElement).getChildren().toArray();
      }

      @Override
      public Object getParent(Object element) {
         return ((ExampleTreeNode) element).getParent();
      }

      @Override
      public boolean hasChildren(Object element) {
         return ((ExampleTreeNode) element).getChildren().size() > 0;
      }

   }

   private static class ExampleTreeNode {
      private final String name;
      private final ExampleTreeNode parent;
      private final List<ExampleTreeNode> children = new ArrayList<>();

      protected ExampleTreeNode(String name, ExampleTreeNode aiNode, boolean enabled, ICheckBoxStateTreeViewer treeViewer) {
         this.name = name;
         this.parent = aiNode;
         if (aiNode != null) {
            aiNode.addChild(this);
         }
         treeViewer.setEnabled(this, enabled);
      }

      public List<ExampleTreeNode> getChildren() {
         return children;
      }

      public String getName() {
         return name;
      }

      public ExampleTreeNode getParent() {
         return parent;
      }

      @Override
      public String toString() {
         return name;
      }

      public void addChild(ExampleTreeNode child) {
         children.add(child);
      }
   }
}
