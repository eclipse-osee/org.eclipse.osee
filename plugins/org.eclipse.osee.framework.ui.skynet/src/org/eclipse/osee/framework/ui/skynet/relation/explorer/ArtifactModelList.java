/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ArtifactModelList {

   private ArrayList<ArtifactModel> artifacts = new ArrayList<>();
   private Set<IArtifactListViewer> changeListeners = new HashSet<>();

   /**
    * Constructor
    */
   public ArtifactModelList() {
      super();
      artifacts = new ArrayList<>();
      changeListeners = new HashSet<>();
   }

   /**
    * Return the collection of ItemTask
    */
   public ArrayList<ArtifactModel> getArtifacts() {
      return artifacts;
   }

   /**
    * Add a new task to the collection of tasks
    */
   public void addArtifact(ArtifactModel artifact, boolean top) {
      if (top) {
         artifacts.add(0, artifact);
      } else {
         artifacts.add(artifacts.size(), artifact);
      }
      Iterator<IArtifactListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext()) {
         iterator.next().addArtifact(artifact);
      }
   }

   /**
    * @param artifact -
    */
   public void removeArtifact(ArtifactModel artifact) {
      artifacts.remove(artifact);
      Iterator<IArtifactListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext()) {
         iterator.next().removeArtifact(artifact);
      }
   }

   @Override
   public String toString() {
      String str = "";
      for (int i = 0; i < artifacts.size(); i++) {
         String name = artifacts.get(i).getName();
         str += "\nTask " + name;
      }
      return str + "\n\n";
   }

   /**
    * @param artifact -
    */
   public void artifactChanged(ArtifactModel artifact) {
      Iterator<IArtifactListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext()) {
         iterator.next().updateArtifact(artifact);
      }
   }

   public void removeChangeListener(IArtifactListViewer viewer) {
      changeListeners.remove(viewer);
   }

   public void addChangeListener(IArtifactListViewer viewer) {
      changeListeners.add(viewer);
   }

   public ArrayList<ArtifactModel> getArtifactModel() {
      return artifacts;
   }

}
