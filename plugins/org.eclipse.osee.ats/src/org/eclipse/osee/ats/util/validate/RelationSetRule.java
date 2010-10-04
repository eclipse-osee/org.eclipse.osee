package org.eclipse.osee.ats.util.validate;

import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.model.type.ArtifactType;

/**
 * @author Donald G. Dunne
 */
public final class RelationSetRule {
   private final IArtifactType artifactType;
   private final Integer minimumRelations;
   private final IRelationEnumeration relationEnum;

   public RelationSetRule(IArtifactType artifactType, IRelationEnumeration relationEnum, Integer minimumRelations) {
      this.artifactType = artifactType;
      this.relationEnum = relationEnum;
      this.minimumRelations = minimumRelations;
   }

   public Integer getMinimumRelations() {
      return minimumRelations;
   }

   public boolean hasArtifactType(ArtifactType artType) {
      return artType.inheritsFrom(artifactType);
   }

   @Override
   public String toString() {
      return "For \"" + artifactType + "\", ensure at least " + minimumRelations + " relations(s) of type \"" + relationEnum + "\" exists";
   }

   public IRelationEnumeration getRelationEnum() {
      return relationEnum;
   }
}