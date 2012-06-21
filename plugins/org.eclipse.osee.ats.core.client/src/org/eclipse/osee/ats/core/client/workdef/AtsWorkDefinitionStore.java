/*
 * Created on Jun 25, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.workdef;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.workdef.api.IAtsWorkDefinitionStore;
import org.eclipse.osee.ats.workdef.api.IAttributeResolver;
import org.eclipse.osee.ats.workdef.api.IUserResolver;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

public class AtsWorkDefinitionStore implements IAtsWorkDefinitionStore {

   public static AtsWorkDefinitionStore instance;
   private WorkDefAttributeResolver attrResolver;
   private WorkDefUserResolver userResolver;

   public AtsWorkDefinitionStore() {
      AtsWorkDefinitionStore.instance = this;
   }

   public static AtsWorkDefinitionStore getInstance() {
      return instance;
   }

   @Override
   public String loadWorkDefinitionString(String workDefId) {
      try {
         return loadWorkDefinitionFromArtifact(workDefId);
      } catch (Exception ex) {
         throw new IllegalArgumentException(ex.getLocalizedMessage());
      }
   }

   @Override
   public IAttributeResolver getAttributeResolver() {
      if (attrResolver == null) {
         attrResolver = new WorkDefAttributeResolver();
      }
      return attrResolver;
   }

   @Override
   public IUserResolver getUserResolver() {
      if (userResolver == null) {
         userResolver = new WorkDefUserResolver();
      }
      return userResolver;
   }

   private Artifact getWorkDefinitionArtifact(String name) throws OseeCoreException {
      Artifact artifact = null;
      try {
         artifact =
            ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition, name,
               BranchManager.getCommonBranch());
         return artifact;
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return null;
   }

   private String loadWorkDefinitionFromArtifact(String name) throws OseeCoreException {
      Artifact artifact = null;
      try {
         artifact = getWorkDefinitionArtifact(name);
         return loadWorkDefinitionFromArtifact(artifact);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      } catch (Exception ex) {
         throw new OseeWrappedException(String.format("Error loading AtsDsl [%s] from Artifact", name), ex);
      }
      return null;
   }

   private String loadWorkDefinitionFromArtifact(Artifact artifact) throws OseeCoreException {
      String modelText = artifact.getAttributesToString(AtsAttributeTypes.DslSheet);
      return modelText;
   };

}
