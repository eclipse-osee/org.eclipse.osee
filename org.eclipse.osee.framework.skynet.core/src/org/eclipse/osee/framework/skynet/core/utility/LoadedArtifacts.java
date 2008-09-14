/*
 * Created on Sep 13, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;

/**
 * @author Donald G. Dunne
 */
public class LoadedArtifacts {

   private Set<Artifact> artifacts;
   private final Collection<UnloadedArtifact> unloadedArtifacts;

   /**
    * Called when network event passes artifactIds that may or may not be in current client's cache
    * 
    * @param branchId
    * @param artifactIds
    * @param artifactTypeIds
    */
   public LoadedArtifacts(int branchId, Collection<Integer> artifactIds, Collection<Integer> artifactTypeIds) {
      unloadedArtifacts = new ArrayList<UnloadedArtifact>();
      int x = 0;
      Integer[] artTypeIds = artifactTypeIds.toArray(new Integer[artifactTypeIds.size()]);
      for (Integer artId : artifactIds) {
         unloadedArtifacts.add(new UnloadedArtifact(branchId, artId, artTypeIds[x++]));
      }
      this.artifacts = null;
   }

   /**
    * Called when local event is kicked. Since local, all artifacts are, by definition, cached
    * 
    * @param artifacts
    */
   public LoadedArtifacts(Collection<? extends Artifact> artifacts) {
      this.artifacts = new HashSet<Artifact>();
      this.artifacts.addAll(artifacts);
      unloadedArtifacts = new ArrayList<UnloadedArtifact>();
   }

   public List<Integer> getAllArtifactIds() {
      return new ArrayList<Integer>();
   }

   public List<Integer> getAllArtifactTypeIds() {
      return new ArrayList<Integer>();
   }

   public List<UnloadedArtifact> getUnloadedArtifactIds() {
      return new ArrayList<UnloadedArtifact>();
   }

   public synchronized Collection<Artifact> getLoadedArtifacts() throws BranchDoesNotExist, SQLException {
      // If artifacts have not been set, resolve any unloaded artifactIds that exist in current cache
      if (artifacts == null && unloadedArtifacts.size() > 0) {
         artifacts = new HashSet<Artifact>();
         for (UnloadedArtifact unloadedArtifact : new CopyOnWriteArrayList<UnloadedArtifact>(unloadedArtifacts)) {
            Artifact art =
                  ArtifactCache.getActive(unloadedArtifact.getArtifactId(),
                        BranchPersistenceManager.getBranch(unloadedArtifact.getBranchId()));
            if (art != null) {
               unloadedArtifacts.remove(unloadedArtifact);
               artifacts.add(art);
            }

         }
      }
      return artifacts;
   }
}
