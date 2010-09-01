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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChange;

public class XChangeContentProvider implements ITreeContentProvider {
   private static Object[] EMPTY_ARRAY = new Object[0];
   private final List<ArtifactChange> orderedChanges;

   public XChangeContentProvider() {
      super();
      this.orderedChanges = new ArrayList<ArtifactChange>();
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) {
         return ((Collection<?>) parentElement).toArray();
      }
      return EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      if (element instanceof Collection<?>) {
         return true;
      }
      return false;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   private void computeDocOrder(Object[] data) {
      Map<Artifact, ArtifactChange> artChangeMap = new HashMap<Artifact, ArtifactChange>();
      for (Object object : data) {
         if (object instanceof ArtifactChange) {
            ArtifactChange artifactChanged = (ArtifactChange) object;
            Artifact artifact = artifactChanged.getChangeArtifact();
            if (artifact != null) {
               artChangeMap.put(artifact, artifactChanged);
            }
         }
      }
      orderedChanges.clear();
      List<Artifact> sortedArtifact = new ArrayList<Artifact>();

      for (Artifact artifactToSort : artChangeMap.keySet()) {
         sortedArtifact.add(artifactToSort);
      }

      Collections.sort(sortedArtifact, new DefaultHierarchySorter());
      for (Artifact artifact : sortedArtifact) {
         orderedChanges.add(artChangeMap.get(artifact));
      }
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
