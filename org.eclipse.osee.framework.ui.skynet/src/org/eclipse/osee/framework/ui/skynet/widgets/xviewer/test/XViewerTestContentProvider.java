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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.test;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.ui.plugin.util.Displays;

public class XViewerTestContentProvider implements ITreeContentProvider {

   protected Collection<IXViewerTestTask> rootSet = new HashSet<IXViewerTestTask>();
   private final XViewerTest xViewerTest;
   private static Object[] EMPTY_ARRAY = new Object[0];

   public XViewerTestContentProvider(XViewerTest xViewerTest) {
      super();
      this.xViewerTest = xViewerTest;
   }

   public void add(final IXViewerTestTask item) {
      add(Arrays.asList(item));
   }

   public void add(final Collection<? extends IXViewerTestTask> items) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewerTest.getInput() == null) xViewerTest.setInput(rootSet);
            rootSet.addAll(items);
            xViewerTest.refresh();
         };
      });
   }

   public void set(final Collection<? extends IXViewerTestTask> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewerTest.getInput() == null) xViewerTest.setInput(rootSet);
            clear();
            add(arts);
         };
      });
   }

   public void clear() {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewerTest.getInput() == null) xViewerTest.setInput(rootSet);
            rootSet.clear();
            xViewerTest.refresh();
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
      return null;
   }

   public boolean hasChildren(Object element) {
      return false;
   }

   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) return new Object[] {inputElement};
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   @SuppressWarnings("unchecked")
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

   /**
    * @return the rootSet
    */
   public Collection<IXViewerTestTask> getRootSet() {
      return rootSet;
   }

}
