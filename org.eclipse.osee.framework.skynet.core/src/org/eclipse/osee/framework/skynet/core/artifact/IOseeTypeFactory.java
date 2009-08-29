package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

public interface IOseeTypeFactory {

   public ArtifactType createArtifactType(String guid, boolean isAbstract, String artifactTypeName) throws OseeCoreException;

   public AttributeType createAttributeType(String guid, String name, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, int oseeEnumTypeId, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException;

   public RelationType createRelationType(String guid, String typeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isUserOrdered, String defaultOrderTypeGuid) throws OseeCoreException;
}
