package org.eclipse.osee.framework.ui.skynet.Import;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.AttributeBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.GuidBasedArtifactResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.NewArtifactImportResolver;

public enum MatchingStrategy {
   ATTRIBUTE(), GUID(), NONE();

   private MatchingStrategy() {
   }

   public IArtifactImportResolver getResolver(ArtifactType primaryArtifactType, Collection<AttributeType> nonChangingAttributes, boolean createNewIfNotExist, boolean deleteUnmatchedArtifacts) throws OseeCoreException {
      ArtifactType secondaryArtifactType = ArtifactTypeManager.getType("Heading");
      if (this == ATTRIBUTE) {
         return new AttributeBasedArtifactResolver(primaryArtifactType, secondaryArtifactType,
               nonChangingAttributes, createNewIfNotExist, deleteUnmatchedArtifacts);
      } else if (this == GUID) {
         return new GuidBasedArtifactResolver(primaryArtifactType, secondaryArtifactType, createNewIfNotExist,
               deleteUnmatchedArtifacts);
      } else {
         return new NewArtifactImportResolver(primaryArtifactType, secondaryArtifactType);
      }
   }
}
