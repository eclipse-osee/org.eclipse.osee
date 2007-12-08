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
package org.eclipse.osee.framework.ui.skynet.relation.explorer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ArtifactModelList {

   private ArrayList<ArtifactModel> artifacts = new ArrayList<ArtifactModel>();
   private Set<IArtifactListViewer> changeListeners = new HashSet<IArtifactListViewer>();

   /**
    * Constructor
    */
   public ArtifactModelList() {
      super();
      artifacts = new ArrayList<ArtifactModel>();
      changeListeners = new HashSet<IArtifactListViewer>();
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
      if (top)
         artifacts.add(0, artifact);
      else
         artifacts.add(artifacts.size(), artifact);
      Iterator<IArtifactListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         iterator.next().addArtifact(artifact);
   }

   /**
    * @param artifact -
    */
   public void removeArtifact(ArtifactModel artifact) {
      artifacts.remove(artifact);
      Iterator<IArtifactListViewer> iterator = changeListeners.iterator();
      while (iterator.hasNext())
         iterator.next().removeArtifact(artifact);
   }

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
      while (iterator.hasNext())
         iterator.next().updateArtifact(artifact);
   }

   /**
    * @param viewer
    */
   public void removeChangeListener(IArtifactListViewer viewer) {
      changeListeners.remove(viewer);
   }

   /**
    * @param viewer
    */
   public void addChangeListener(IArtifactListViewer viewer) {
      changeListeners.add(viewer);
   }

   public ArrayList<ArtifactModel> getArtifactModel() {
      return artifacts;
   }

}
