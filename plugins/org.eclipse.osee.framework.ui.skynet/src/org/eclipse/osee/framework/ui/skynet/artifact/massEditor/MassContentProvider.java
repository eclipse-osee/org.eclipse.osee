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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class MassContentProvider implements ITreeContentProvider {
   protected Collection<Artifact> rootSet = new HashSet<>();
   private final MassXViewer xViewer;
   private static Object[] EMPTY_ARRAY = new Object[0];

   public MassContentProvider(MassXViewer xViewer) {
      super();
      this.xViewer = xViewer;
   }

   public void add(final Artifact item) {
      add(Arrays.asList(item));
   }

   public void add(final Collection<? extends Artifact> items) {
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

   public void set(final Collection<? extends Artifact> arts) {
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

   public void updateAll(final Collection<? extends Object> arts) {
      if (arts.isEmpty()) {
         return;
      }
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xViewer.getInput() == null) {
               xViewer.setInput(rootSet);
            }
            for (Object art : arts) {
               xViewer.update(art, null);
            }
         };
      });
   }

   public void remove(final EventBasicGuidArtifact art) {
      removeAll(Arrays.asList(art));
   }

   public void removeAll(final Collection<? extends EventBasicGuidArtifact> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            Iterator<Artifact> iterator = rootSet.iterator();
            while (iterator.hasNext()) {
               Artifact artifact = iterator.next();
               for (EventBasicGuidArtifact art : arts) {
                  if (art.equals(artifact)) {
                     iterator.remove();
                  }
               }
            }

            if (xViewer.getInput() == null) {
               xViewer.setInput(rootSet);
            }
            xViewer.refresh();
         };
      });
   }

   public void removeAllArts(final Collection<? extends Artifact> arts) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (xViewer.getInput() == null) {
               xViewer.setInput(rootSet);
            }
            rootSet.removeAll(arts);
            xViewer.refresh();
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

   @SuppressWarnings("rawtypes")
   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof Collection) {
         return true;
      }
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

}
