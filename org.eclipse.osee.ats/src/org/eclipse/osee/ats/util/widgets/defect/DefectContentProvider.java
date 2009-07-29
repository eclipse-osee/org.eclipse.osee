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
package org.eclipse.osee.ats.util.widgets.defect;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;

public class DefectContentProvider implements ITreeContentProvider {

   protected Collection<DefectItem> rootSet = new HashSet<DefectItem>();
   private final DefectXViewer xViewer;
   private static Object[] EMPTY_ARRAY = new Object[0];
   private SkynetGuiDebug debug = new SkynetGuiDebug(false, "DefectContentProvider");

   public DefectContentProvider(DefectXViewer WorldXViewer) {
      super();
      this.xViewer = WorldXViewer;
   }

   public void add(final DefectItem item) {
      add(Arrays.asList(item));
   }

   public void add(final Collection<? extends DefectItem> items) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null)
               xViewer.setInput(rootSet);
            rootSet.addAll(items);
            xViewer.refresh();
         };
      });
   }

   public void set(final Collection<? extends DefectItem> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null)
               xViewer.setInput(rootSet);
            clear();
            add(arts);
         };
      });
   }

   public void clear() {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null)
               xViewer.setInput(rootSet);
            rootSet.clear();
            xViewer.refresh();
         };
      });
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      debug.report("getParent");
      return null;
   }

   public boolean hasChildren(Object element) {
      debug.report("hasChildren");
      return false;
   }

   public Object[] getElements(Object inputElement) {
      debug.report("getElements");
      if (inputElement instanceof String)
         return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   /**
    * @return the rootSet
    */
   public Collection<DefectItem> getRootSet() {
      return rootSet;
   }

}
