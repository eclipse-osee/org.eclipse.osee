/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.navigate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxTreeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class FilteredDialogExampleUtil {

   private static MyTask child3;
   private static MyTask parent11;

   protected static void openAndReport(FilteredCheckboxTreeDialog dialog, String title) {
      int result = dialog.open();
      XResultData rd = new XResultData();
      rd.log("RESULT=" + (result == 0 ? "Ok" : "Cancel"));
      Collection<Object> checked = dialog.getChecked();
      for (Object obj : checked) {
         rd.log("SELECTED=" + obj.toString());
      }
      if (checked.size() == 0) {
         rd.log("NONE SELECTED");
      }
      XResultDataUI.report(rd, title);
   }

   protected static void openAndReport(FilteredTreeDialog dialog, String title) {
      int result = dialog.open();
      XResultData rd = new XResultData();
      rd.log("RESULT=" + (result == 0 ? "Ok" : "Cancel"));
      Collection<Object> selected = dialog.getSelected();
      for (Object obj : selected) {
         rd.log("SELECTED=" + obj.toString());
      }
      if (dialog.getSelected().size() == 0) {
         rd.log("NONE SELECTED");
      }
      XResultDataUI.report(rd, title);
   }

   protected static List<MyTask> getInput() {
      List<MyTask> tasks = new ArrayList<>();
      MyTask parent1 = new MyTask("First One");
      tasks.add(parent1);
      MyTask child1 = new MyTask("Child this 1", parent1);
      parent1.addChild(child1);
      MyTask child2 = new MyTask("Child that 2", parent1);
      parent1.addChild(child2);
      child3 = new MyTask("Child and 3", parent1);
      parent1.addChild(child3);
      MyTask child4 = new MyTask("Child what 4", parent1);
      parent1.addChild(child4);

      parent11 = new MyTask("Second One");
      tasks.add(parent11);
      MyTask child11 = new MyTask("Child this 11", parent11);
      parent11.addChild(child11);
      MyTask child21 = new MyTask("Child that 21", parent11);
      parent11.addChild(child21);
      MyTask child31 = new MyTask("Child and 31", parent11);
      parent11.addChild(child31);
      MyTask child41 = new MyTask("Child what 41", parent11);
      parent11.addChild(child41);

      MyTask parent111 = new MyTask("Third One");
      tasks.add(parent111);
      MyTask child111 = new MyTask("Child this 111", parent111);
      parent111.addChild(child111);
      MyTask child211 = new MyTask("Child that 211", parent111);
      parent111.addChild(child211);
      MyTask child311 = new MyTask("Child and 311", parent111);
      parent111.addChild(child311);
      MyTask child411 = new MyTask("Child what 411", parent111);
      parent111.addChild(child411);
      return tasks;
   }
   public static class MyTask {

      String name;
      MyTask parent;
      List<MyTask> children = new ArrayList<>();

      public MyTask(String name) {
         this.name = name;
      }

      public MyTask(String name, MyTask parent) {
         this.name = name;
         this.parent = parent;
      }

      @Override
      public String toString() {
         return name;
      }

      public void addChild(MyTask child) {
         children.add(child);
      }

      public MyTask getParent() {
         return parent;
      }

      public List<MyTask> getChildren() {
         return children;
      }
   }
   public static class MyViewSorter extends ViewerComparator {

      @Override
      public int compare(Viewer viewer, Object o1, Object o2) {
         return super.compare(viewer, o1, o2);
      }

   }
   public static class FilterLabelProvider implements ILabelProvider {

      @Override
      public Image getImage(Object arg0) {
         return null;
      }

      @Override
      public String getText(Object arg0) {
         if (arg0 == null) {
            return "";
         }
         try {
            return arg0.toString();
         } catch (OseeCoreException ex) {
            return ex.getLocalizedMessage();
         }
      }

      @Override
      public void addListener(ILabelProviderListener arg0) {
         // do nothing
      }

      @Override
      public void dispose() {
         // do nothing
      }

      @Override
      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      @Override
      public void removeListener(ILabelProviderListener arg0) {
         // do nothing
      }
   }
   public static class MyTreeContentProvider implements ITreeContentProvider {

      public MyTreeContentProvider() {
         super();
      }

      @Override
      public Object[] getChildren(Object parentElement) {
         if (parentElement instanceof Object[]) {
            return (Object[]) parentElement;
         }
         if (parentElement instanceof Collection) {
            return ((Collection<?>) parentElement).toArray();
         }
         if (parentElement instanceof MyTask) {
            return ((MyTask) parentElement).getChildren().toArray();
         }

         return new Object[] {};
      }

      @Override
      public Object getParent(Object element) {
         if (element instanceof MyTask) {
            return ((MyTask) element).getParent();
         }
         return null;
      }

      @Override
      public boolean hasChildren(Object element) {
         if (element instanceof MyTask) {
            return !((MyTask) element).getChildren().isEmpty();
         }
         return false;
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

   }

   public static MyTask getChild3() {
      return child3;
   }

   public static MyTask getParent11() {
      return parent11;
   }

}