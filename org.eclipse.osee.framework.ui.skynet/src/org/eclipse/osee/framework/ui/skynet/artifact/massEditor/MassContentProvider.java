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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.SkynetDebug;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;

public class MassContentProvider implements ITreeContentProvider {

   protected Collection<MassArtifactItem> rootSet = new HashSet<MassArtifactItem>();
   private final MassXViewer xViewer;
   private static Object[] EMPTY_ARRAY = new Object[0];
   private SkynetDebug debug = new SkynetDebug(false, "WorldTreeContentProvider");

   public MassContentProvider(MassXViewer xViewer) {
      super();
      this.xViewer = xViewer;
   }

   public void add(final MassArtifactItem item) {
      add(Arrays.asList(new MassArtifactItem[] {item}));
   }

   public void add(final Collection<? extends MassArtifactItem> items) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            rootSet.addAll(items);
            xViewer.refresh();
         };
      });
   }

   public void set(final Collection<? extends MassArtifactItem> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            if (xViewer.getInput() == null) xViewer.setInput(rootSet);
            clear();
            add(arts);
         };
      });
   }

   public void remove(final Artifact art) {
      remove(Arrays.asList(new Artifact[] {art}));
   }

   public void remove(final Collection<? extends Artifact> arts) {
      if (xViewer.getInput() == null) xViewer.setInput(rootSet);
      ArrayList<MassArtifactItem> delItems = new ArrayList<MassArtifactItem>();
      delItems.addAll(rootSet);
      for (Artifact art : arts) {
         for (MassArtifactItem wai : rootSet)
            if (wai.getArtifact().equals(art)) delItems.add(wai);
      }
      removeItems(delItems);
   }

   public void removeItems(final Collection<? extends MassArtifactItem> arts) {
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
            for (MassArtifactItem wai : rootSet)
               wai.dispose();
            rootSet.clear();
            xViewer.refresh();
         };
      });
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      debug.report("getChildren");
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      if (parentElement instanceof MassArtifactItem) {
         return ((MassArtifactItem) parentElement).getChildren();
      }
      return EMPTY_ARRAY;
   }

   public Object getParent(Object element) {
      debug.report("getParent");
      if (element instanceof MassArtifactItem) {
         return ((MassArtifactItem) element).getParentItem();
      }
      return null;
   }

   public boolean hasChildren(Object element) {
      debug.report("hasChildren");
      if (element instanceof Collection) return true;
      return false;
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

}
