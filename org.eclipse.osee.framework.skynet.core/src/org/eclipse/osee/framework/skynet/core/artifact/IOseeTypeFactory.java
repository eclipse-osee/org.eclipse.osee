package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

public interface IOseeTypeFactory {

   public ArtifactType createArtifactType(int typeId, String guid, boolean isAbstract, String artifactTypeName);

   public AttributeType createAttributeType(int typeId, String guid, boolean isAbstract, String artifactTypeName);

   public RelationType createRelationType(int typeId, String guid, boolean isAbstract, String artifactTypeName);
}
