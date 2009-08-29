package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.List;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

public interface IOseeTypeFactory {

   public ArtifactType createArtifactType(String guid, boolean isAbstract, String artifactTypeName, OseeTypeCache oseeTypeCache) throws OseeCoreException;

   public AttributeType createAttributeType(String guid, String name, String baseAttributeTypeId, String attributeProviderNameId, Class<? extends Attribute<?>> baseAttributeClass, Class<? extends IAttributeDataProvider> providerAttributeClass, String fileTypeExtension, String defaultValue, OseeEnumType oseeEnumType, int minOccurrences, int maxOccurrences, String tipText, String taggerId) throws OseeCoreException;

   public RelationType createRelationType(String guid, String typeName, String sideAName, String sideBName, ArtifactType artifactTypeSideA, ArtifactType artifactTypeSideB, RelationTypeMultiplicity multiplicity, boolean isUserOrdered, String defaultOrderTypeGuid) throws OseeCoreException;

   public OseeEnumType createEnumType(String guid, String name, List<Pair<String, Integer>> entries) throws OseeCoreException;
}
