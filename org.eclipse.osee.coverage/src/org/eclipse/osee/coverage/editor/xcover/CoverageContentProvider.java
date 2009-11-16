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
package org.eclipse.osee.coverage.editor.xcover;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.apache.commons.lang.ArrayUtils;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.coverage.merge.MergeItem;
import org.eclipse.osee.coverage.merge.MessageMergeItem;
import org.eclipse.osee.coverage.model.CoverageItem;
import org.eclipse.osee.coverage.model.CoveragePackageBase;
import org.eclipse.osee.coverage.model.CoverageUnit;
import org.eclipse.osee.coverage.model.ICoverage;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;

public class CoverageContentProvider implements ITreeContentProvider {

   protected Collection<ICoverage> rootSet = new HashSet<ICoverage>();
   private final CoverageXViewer xViewer;
   private final SkynetGuiDebug debug = new SkynetGuiDebug(false, "CoverageContentProvider");

   public CoverageContentProvider(CoverageXViewer coverageXViewer) {
      super();
      this.xViewer = coverageXViewer;
   }

   public void add(final ICoverage item) {
      add(Arrays.asList(item));
   }

   public void add(final Collection<? extends ICoverage> items) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.addAll(items);
            xViewer.refresh();
         };
      });
   }

   public void set(final Collection<? extends ICoverage> coverages) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            clear();
            add(coverages);
         };
      });
   }

   public void remove(final ICoverage coverage) {
      remove(Arrays.asList(coverage));
   }

   public void remove(final Collection<? extends ICoverage> coverages) {
      if (xViewer.getInput() == null) xViewer.setInput(rootSet);
      ArrayList<ICoverage> delItems = new ArrayList<ICoverage>();
      delItems.addAll(rootSet);
      for (ICoverage coverage : coverages) {
         for (ICoverage currCoverage : rootSet)
            if (coverage.equals(currCoverage)) delItems.add(currCoverage);
      }
      removeItems(delItems);
   }

   public void removeItems(final Collection<? extends ICoverage> coverages) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.removeAll(coverages);
            xViewer.refresh();
         };
      });
   }

   public void clear() {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.clear();
            xViewer.refresh();
         };
      });
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof CoveragePackageBase) {
         Collection<?> children = ((CoveragePackageBase) parentElement).getChildren();
         return children.toArray(new Object[children.size()]);
      }
      if (parentElement instanceof CoverageUnit) {
         Collection<?> children = ((CoverageUnit) parentElement).getChildren();
         return children.toArray(new Object[children.size()]);
      }
      if (parentElement instanceof MergeItem) {
         Collection<?> children = ((MergeItem) parentElement).getChildren();
         return children.toArray(new Object[children.size()]);
      }
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      return ArrayUtils.EMPTY_OBJECT_ARRAY;
   }

   public Object getParent(Object element) {
      if (element instanceof CoverageUnit) {
         return ((CoverageUnit) element).getParent();
      }
      if (element instanceof CoverageItem) {
         return ((CoverageItem) element).getParent();
      }
      return null;
   }

   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   public Object[] getElements(Object inputElement) {
      debug.report("getElements");
      if (inputElement instanceof MessageMergeItem) return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   public Collection<ICoverage> getRootSet() {
      return rootSet;
   }

}
