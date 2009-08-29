package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

public interface IOseeTypeDataAccessor {

   public void loadAllTypeValidity(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void loadAllAttributeTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void loadAllRelationTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void loadAllArtifactTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void loadAllOseeEnumTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void storeValidity(List<Object[]> datas) throws OseeCoreException;

   public void storeTypeInheritance(ArtifactType artifactType, Set<ArtifactType> superTypes) throws OseeCoreException;

   public void storeArtifactType(Collection<ArtifactType> artifactType) throws OseeCoreException;

   public void storeAttributeType(Collection<AttributeType> attributeType) throws OseeCoreException;

   public void storeRelationType(Collection<RelationType> relationType) throws OseeCoreException;

   public void storeOseeEnumType(Collection<OseeEnumType> oseeEnumType) throws OseeCoreException;

}
