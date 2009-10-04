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
import org.eclipse.osee.coverage.editor.ICoverageEditorItem;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.util.SkynetGuiDebug;

public class CoverageContentProvider implements ITreeContentProvider {

   protected Collection<ICoverageEditorItem> rootSet = new HashSet<ICoverageEditorItem>();
   private final CoverageXViewer xViewer;
   private final SkynetGuiDebug debug = new SkynetGuiDebug(false, "CoverageContentProvider");

   public CoverageContentProvider(CoverageXViewer coverageXViewer) {
      super();
      this.xViewer = coverageXViewer;
   }

   public void add(final ICoverageEditorItem item) {
      add(Arrays.asList(item));
   }

   public void add(final Collection<? extends ICoverageEditorItem> items) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.addAll(items);
            xViewer.refresh();
         };
      });
   }

   public void set(final Collection<? extends ICoverageEditorItem> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            clear();
            add(arts);
         };
      });
   }

   public void remove(final Artifact art) {
      remove(Arrays.asList(art));
   }

   public void remove(final Collection<? extends Artifact> arts) {
      if (xViewer.getInput() == null) xViewer.setInput(rootSet);
      ArrayList<ICoverageEditorItem> delItems = new ArrayList<ICoverageEditorItem>();
      delItems.addAll(rootSet);
      for (Artifact art : arts) {
         for (ICoverageEditorItem wai : rootSet)
            if (wai.equals(art)) delItems.add(wai);
      }
      removeItems(delItems);
   }

   public void removeItems(final Collection<? extends ICoverageEditorItem> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.remove(arts);
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
      if (parentElement instanceof ICoverageEditorItem) {
         return ((ICoverageEditorItem) parentElement).getChildren();
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
      debug.report("getParent");
      return null;
   }

   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   public Object[] getElements(Object inputElement) {
      debug.report("getElements");
      if (inputElement instanceof String) return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   public Collection<ICoverageEditorItem> getRootSet() {
      return rootSet;
   }

}
