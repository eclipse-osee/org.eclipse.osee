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
package org.eclipse.osee.ats.world.search;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.world.WorldXViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;

/**
 * @author Donald G. Dunne
 */
public abstract class WorldSearchItem {

   private final String name;
   private Set<Artifact> results = new HashSet<Artifact>();
   private WorldXViewer xViewer;
   protected boolean cancelled = false;
   protected boolean loadWorldView = true;
   protected ArtifactPersistenceManager apm = ArtifactPersistenceManager.getInstance();

   public WorldSearchItem(String name) {
      super();
      this.name = name;
      this.cancelled = false;
   }

   public String getName() {
      return name;
   }

   /**
    * Method called to display the current search in the view. Override to provide more information about selected
    * values (eg MyWorld)
    * 
    * @return selected name
    */
   public String getSelectedName() {
      return getName();
   }

   public abstract void performSearch() throws SQLException, IllegalArgumentException;

   /**
    * Perform search and return result set without loading in WorldView. This method can be used repeatedly.
    * 
    * @return artifacts resulting from search
    * @throws SQLException
    * @throws IllegalArgumentException
    */
   public Set<Artifact> performSearchGetResults() throws SQLException, IllegalArgumentException {
      return performSearchGetResults(false);
   }

   public Set<Artifact> performSearchGetResults(boolean loadWorldView) throws SQLException, IllegalArgumentException {
      this.loadWorldView = loadWorldView;
      results.clear();
      performSearch();
      return results;
   }

   public Set<Artifact> performUiSearchGetResults() throws SQLException, IllegalArgumentException {
      loadWorldView = false;
      results.clear();
      if (!performUI()) return new HashSet<Artifact>();
      performSearch();
      return results;
   }

   public Collection<Artifact> performSearch(WorldXViewer xViewer) throws SQLException, IllegalArgumentException {
      this.xViewer = xViewer;
      return performSearchGetResults(xViewer != null);
   }

   public boolean performUI() {
      return true;
   }

   public void addResultArtifact(Artifact artifact) {
      if ((!(artifact instanceof ActionArtifact)) && (!(artifact instanceof StateMachineArtifact))) {
         ArtifactEditor.editArtifact(artifact);
         return;
      }
      if (!results.contains(artifact)) {
         results.add(artifact);
         if (loadWorldView && xViewer != null) xViewer.add(artifact);
      }
   }

   public void addResultArtifacts(Collection<Artifact> artifacts) {
      ArrayList<Artifact> addedArts = new ArrayList<Artifact>();
      for (Artifact artifact : artifacts) {
         if ((!(artifact instanceof ActionArtifact)) && (!(artifact instanceof StateMachineArtifact))) {
            ArtifactEditor.editArtifact(artifact);
            continue;
         }
         if (!results.contains(artifact)) {
            if (isCancelled()) return;
            results.add(artifact);
            addedArts.add(artifact);
         }
      }
      if (loadWorldView && xViewer != null && addedArts.size() > 0) xViewer.add(addedArts);
   }

   public Set<Artifact> getResultArtifacts() {
      return results;
   }

   public boolean isCancelled() {
      return cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public boolean isLoadWorldView() {
      return loadWorldView;
   }

   /**
    * By default, performSearch loads worldview with results. Set to false to perform search and use getResultArtifacts
    * to get result set.
    * 
    * @param loadWorldView
    */
   public void setLoadWorldView(boolean loadWorldView) {
      this.loadWorldView = loadWorldView;
   }

}
