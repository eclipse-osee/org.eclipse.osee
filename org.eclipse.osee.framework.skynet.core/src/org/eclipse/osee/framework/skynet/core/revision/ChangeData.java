/*
 * Created on Oct 3, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.revision;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.ArtifactChanged;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.RelationChanged;

/**
 * Collection of changes from working branch or transactionId from committed branch.
 * 
 * @author Donald G. Dunne
 */
public class ChangeData {

   Collection<Change> changes;

   /**
    * @return the changes
    */
   public Collection<Change> getChanges() {
      return changes;
   }

   public static enum KindType {
      Artifact, Relation, ArtifactOrRelation, RelationOnly
   };
   public boolean artifactsBulkLoaded = false;

   public ChangeData(Collection<Change> changes) {
      this.changes = changes;
   }

   public Collection<Change> getChangesByName(String name) throws OseeCoreException {
      try {
         List<Change> matches = new ArrayList<Change>();
         for (Change change : changes) {
            if (change.getArtifactName().equals(name)) {
               matches.add(change);
            }
         }
         return matches;
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public Collection<Artifact> getArtifactsByName(String name) throws OseeCoreException {
      try {
         List<Artifact> matches = new ArrayList<Artifact>();
         for (Change change : changes) {
            if (change.getArtifactName().equals(name)) {
               matches.add(change.getArtifact());
            }
         }
         return matches;
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Return artifacts of kind and modType.
    * 
    * @param kindType
    * @param modificationType
    * @return artifacts
    * @throws OseeCoreException
    */
   public Collection<Artifact> getArtifacts(KindType kindType, ModificationType... modificationType) throws OseeCoreException {
      if (!artifactsBulkLoaded) {
         artifactsBulkLoaded = true;
      }
      if (kindType == KindType.RelationOnly) {
         return getArtifactsRelationOnly(modificationType);
      }
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<ModificationType> modTypes = Collections.getAggregate(modificationType);
      if (modTypes.size() == 0) {
         throw new OseeCoreException("ModificationType must be specified");
      }
      if (kindType == KindType.Artifact || kindType == KindType.ArtifactOrRelation || kindType == KindType.Relation) {
         if (changes != null) {
            for (Change change : changes) {
               if ((kindType == KindType.Artifact || kindType == KindType.ArtifactOrRelation) && (change instanceof ArtifactChanged)) {
                  if (modTypes.contains(change.getModificationType())) {
                     artifacts.add(change.getArtifact());
                  }
               }
               // 
               else if ((kindType == KindType.Relation || kindType == KindType.ArtifactOrRelation) && (change instanceof RelationChanged)) {
                  if (modTypes.contains(change.getModificationType())) {
                     artifacts.add(((RelationChanged) change).getArtifact());
                     artifacts.add(((RelationChanged) change).getBArtifact());
                  }
               }
            }
         }
      }
      return artifacts;
   }

   private Collection<Artifact> getArtifactsRelationOnly(ModificationType... modificationType) throws OseeCoreException {
      Collection<Artifact> artMod = getArtifacts(KindType.Artifact, modificationType);
      Collection<Artifact> relMod = getArtifacts(KindType.Relation, modificationType);
      return Collections.setComplement(relMod, artMod);
   }

   @Override
   public String toString() {
      try {
         StringBuffer sb = new StringBuffer();
         for (KindType kindType : KindType.values()) {
            for (ModificationType modificationType : ModificationType.values()) {
               sb.append("Kind: " + kindType + " ModType: " + modificationType.getDisplayName() + " Num: " + getArtifacts(
                     kindType, modificationType).size() + "\n");
            }
         }
         return sb.toString();
      } catch (OseeCoreException ex) {
         return ex.getLocalizedMessage();
      }
   }
}
