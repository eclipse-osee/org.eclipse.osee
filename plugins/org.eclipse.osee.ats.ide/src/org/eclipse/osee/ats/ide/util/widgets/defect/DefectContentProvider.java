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
package org.eclipse.osee.ats.ide.util.widgets.defect;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.review.ReviewDefectItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class DefectContentProvider implements ITreeContentProvider {

   protected Collection<ReviewDefectItem> rootSet = new HashSet<>();
   private final DefectXViewer xViewer;

   public DefectContentProvider(DefectXViewer WorldXViewer) {
      super();
      this.xViewer = WorldXViewer;
   }

   public void add(final ReviewDefectItem item) {
      add(Arrays.asList(item));
   }

   public void add(final Collection<? extends ReviewDefectItem> items) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xViewer.getInput() == null) {
               xViewer.setInput(rootSet);
            }
            rootSet.addAll(items);
            xViewer.refresh();
         };
      });
   }

   public void set(final Collection<? extends ReviewDefectItem> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xViewer.getInput() == null) {
               xViewer.setInput(rootSet);
            }
            clear();
            add(arts);
         };
      });
   }

   public void clear() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xViewer.getInput() == null) {
               xViewer.setInput(rootSet);
            }
            rootSet.clear();
            xViewer.refresh();
         };
      });
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      return Collections.EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return false;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) {
         return new Object[] {inputElement};
      }
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

   /**
    * @return the rootSet
    */
   public Collection<ReviewDefectItem> getRootSet() {
      return rootSet;
   }

}
