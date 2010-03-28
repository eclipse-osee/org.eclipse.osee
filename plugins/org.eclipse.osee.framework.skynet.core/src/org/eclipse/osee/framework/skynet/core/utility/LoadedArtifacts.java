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
package org.eclipse.osee.framework.skynet.core.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.data.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;

/**
 * @author Donald G. Dunne
 */
public class LoadedArtifacts {

   private Set<Artifact> artifacts;
   private final Collection<UnloadedArtifact> unloadedArtifacts;
   private Set<String> allArtifactGuids;
   private Set<String> allUnloadedArtifactGuids;
   private Set<Integer> allArtifactIds;
   private Set<Integer> allUnloadedArtifactIds;
   private Set<Integer> allArtifactTypeIds;
   private Set<IBasicGuidArtifact> artifactChanges = new HashSet<IBasicGuidArtifact>();

   /**
    * Called when network event passes artifactIds that may or may not be in current client's cache
    * 
    * @throws OseeCoreException
    */
   public LoadedArtifacts(int branchId, Collection<Integer> artifactIds, Collection<String> artifactGuids, Collection<Integer> artifactTypeIds) throws OseeCoreException {
      unloadedArtifacts = new ArrayList<UnloadedArtifact>();
      int x = 0;
      String branchGuid = BranchManager.getBranch(branchId).getGuid();
      Integer[] artTypeIds = artifactTypeIds.toArray(new Integer[artifactTypeIds.size()]);
      String[] artGuids = artifactGuids.toArray(new String[artifactGuids.size()]);
      String artTypeGuid = ArtifactTypeManager.getType(artTypeIds[x]).getGuid();
      for (Integer artId : artifactIds) {
         unloadedArtifacts.add(new UnloadedArtifact(branchGuid, artTypeIds[x], artTypeGuid, artId, artGuids[x]));
         artifactChanges.add(new DefaultBasicGuidArtifact(branchGuid, artTypeGuid, artGuids[x]));
         x++;
      }
      this.artifacts = null;
   }

   /**
    * Called when local event is kicked. Since local, all artifacts are, by definition, cached
    */
   public LoadedArtifacts(Collection<? extends Artifact> artifacts) {
      this.artifacts = new HashSet<Artifact>();
      this.artifacts.addAll(artifacts);
      for (Artifact artifact : artifacts) {
         artifactChanges.add(artifact.getBasicGuidArtifact());
      }
      unloadedArtifacts = new ArrayList<UnloadedArtifact>();
   }

   /**
    * Called when local event is kicked. Since local, all artifacts are, by definition, cached
    * 
    * @param artifacts
    */
   public LoadedArtifacts(Artifact artifact) {
      this.artifacts = new HashSet<Artifact>();
      if (artifact != null) {
         this.artifacts.add(artifact);
      }
      unloadedArtifacts = new ArrayList<UnloadedArtifact>();
   }

   public static LoadedArtifacts createEmptyLoadedArtifacts() {
      return new LoadedArtifacts((Artifact) null);
   }

   public boolean isNotForBranch(Branch branch) throws OseeCoreException {
      Collection<Artifact> loadedArtifacts = getLoadedArtifacts();
      if (loadedArtifacts.size() > 0) {
         return !getLoadedArtifacts().iterator().next().getBranch().equals(branch);
      }
      return false;
   }

   @Override
   public String toString() {
      return "LoadedArtifacts - " + (this.artifacts == null ? "" : this.artifacts.size() + " arts - ") + (this.unloadedArtifacts == null ? "" : this.unloadedArtifacts.size() + " unlodaded");
   }

   public synchronized Collection<Integer> getAllArtifactIds() {
      if (allArtifactIds == null) {
         allArtifactIds = new HashSet<Integer>(artifacts.size() + unloadedArtifacts.size());
         for (Artifact artifact : this.artifacts) {
            allArtifactIds.add(artifact.getArtId());
         }
         for (UnloadedArtifact unloadedArtifact : unloadedArtifacts) {
            allArtifactIds.add(unloadedArtifact.getArtifactId());
         }
      }
      return allArtifactIds;
   }

   public synchronized Collection<String> getAllArtifactGuids() {
      if (allArtifactGuids == null) {
         allArtifactGuids = new HashSet<String>(unloadedArtifacts.size() + unloadedArtifacts.size());
         for (Artifact artifact : this.artifacts) {
            allArtifactGuids.add(artifact.getGuid());
         }
         for (UnloadedArtifact unloadedArtifact : unloadedArtifacts) {
            allArtifactGuids.add(unloadedArtifact.getGuid());
         }
      }
      return allArtifactGuids;
   }

   public synchronized Collection<Integer> getAllArtifactTypeIds() {
      if (allArtifactTypeIds == null) {
         allArtifactTypeIds = new HashSet<Integer>(unloadedArtifacts.size() + unloadedArtifacts.size());
         for (Artifact artifact : this.artifacts) {
            allArtifactTypeIds.add(artifact.getArtTypeId());
         }
         for (UnloadedArtifact unloadedArtifact : unloadedArtifacts) {
            allArtifactTypeIds.add(unloadedArtifact.getArtifactTypeId());
         }
      }
      return allArtifactTypeIds;
   }

   public Collection<Integer> getUnloadedArtifactIds() {
      if (allUnloadedArtifactIds == null) {
         allUnloadedArtifactIds = new HashSet<Integer>(unloadedArtifacts.size());
         for (UnloadedArtifact unloadedArtifact : unloadedArtifacts) {
            allUnloadedArtifactIds.add(unloadedArtifact.getArtifactId());
         }
      }
      return allUnloadedArtifactIds;
   }

   public Collection<Integer> getUnloadedArtifactGuids() {
      if (allUnloadedArtifactGuids == null) {
         allUnloadedArtifactGuids = new HashSet<String>(unloadedArtifacts.size());
         for (UnloadedArtifact unloadedArtifact : unloadedArtifacts) {
            allUnloadedArtifactGuids.add(unloadedArtifact.getGuid());
         }
      }
      return allUnloadedArtifactIds;
   }

   public synchronized Collection<Artifact> getLoadedArtifacts() throws OseeCoreException {
      // If artifacts have not been set, resolve any unloaded artifactIds that exist in current cache
      if (artifacts == null) {
         artifacts = new HashSet<Artifact>();
         if (unloadedArtifacts.size() > 0) {
            for (UnloadedArtifact unloadedArtifact : new CopyOnWriteArrayList<UnloadedArtifact>(unloadedArtifacts)) {
               Artifact art =
                     ArtifactCache.getActive(unloadedArtifact.getArtifactId(),
                           BranchManager.getBranch(unloadedArtifact.getBranchGuid()));
               if (art != null) {
                  unloadedArtifacts.remove(unloadedArtifact);
                  artifacts.add(art);
               }

            }
         }
      }
      return artifacts;
   }

   public Set<IBasicGuidArtifact> getArtifactChanges() throws OseeCoreException {
      return artifactChanges;
   }
}
