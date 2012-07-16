package org.eclipse.osee.ats.core.client.config.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.core.client.config.AtsObjectsClient;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public abstract class ArtifactAtsObjectStore {
   protected Artifact artifact;
   protected IOseeBranch branch;
   private final IArtifactType artifactType;
   protected IAtsObject atsObject;

   public ArtifactAtsObjectStore(IAtsObject atsObject, IArtifactType artifactType, IOseeBranch branch) {
      this.atsObject = atsObject;
      this.artifactType = artifactType;
      this.branch = branch;
   }

   public Artifact getArtifact() throws OseeCoreException {
      if (artifact == null) {
         try {
            artifact = ArtifactQuery.getArtifactFromId(atsObject.getGuid(), branch);
         } catch (ArtifactDoesNotExist ex) {
            // do nothing
         }
      }
      return artifact;
   }

   public Artifact getArtifactOrCreate(SkynetTransaction transaction) throws OseeCoreException {
      if (artifact == null) {
         artifact = getArtifact();
         if (artifact == null) {
            artifact =
               ArtifactTypeManager.addArtifact(artifactType, branch, atsObject.getName(), atsObject.getGuid(),
                  atsObject.getHumanReadableId());
            artifact.persist(transaction);
         }
      }
      saveToArtifact(transaction);
      return artifact;
   }

   /**
    * Overwrite current relations of type side with new atsObject artifact. Artifacts must already exist in system for
    * this method to work. Persist must be done outside this method
    * 
    * @return collection of artifacts that were related
    */
   public Collection<Artifact> setRelationsOfType(Artifact artifact, Collection<? extends IAtsObject> atsObjects, IRelationTypeSide side) throws OseeCoreException {
      List<Artifact> newArts = new ArrayList<Artifact>();
      for (IAtsObject version : atsObjects) {
         Artifact verArt = AtsObjectsClient.getSoleArtifact(version);
         newArts.add(verArt);
      }
      artifact.setRelations(side, newArts);
      return newArts;
   }

   public void save(String saveName) throws OseeCoreException {
      SkynetTransaction transaction = TransactionManager.createTransaction(branch, "ATS Config Save - " + saveName);
      saveToArtifact(transaction);
      transaction.execute();
   }

   public abstract Result saveToArtifact(SkynetTransaction transaction) throws OseeCoreException;

   public IOseeBranch getBranch() {
      return branch;
   }
}