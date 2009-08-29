package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

public interface IOseeTypeDataAccessor {

   public void loadAllTypeValidity(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void loadAllAttributeTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void loadAllRelationTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void loadAllArtifactTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException;

   public void storeValidity(List<Object[]> datas) throws OseeCoreException;

   public void storeTypeInheritance(List<Object[]> datas) throws OseeCoreException;

   public void storeArtifactType(ArtifactType... artifactType) throws OseeCoreException;

   public void storeAttributeType(AttributeType... attributeType) throws OseeCoreException;

   public void storeRelationType(RelationType... relationType) throws OseeCoreException;

}
