package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.skynet.core.artifact.factory.ArtifactFactoryManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

public class OseeTypeFactory implements IOseeTypeFactory {
   private final ArtifactFactoryManager factoryManager;
   private final OseeTypeCache oseeTypeCache;

   public OseeTypeFactory(OseeTypeCache oseeTypeCache) {
      this.factoryManager = new ArtifactFactoryManager();
      this.oseeTypeCache = oseeTypeCache;
   }

   public ArtifactType createArtifactType(int artTypeId, boolean isAbstract, String name) {
      ArtifactType artifactType = new ArtifactType(isAbstract, name, factoryManager);
      oseeTypeCache.cacheArtifactType(artifactType);
      return artifactType;
   }

   @Override
   public ArtifactType createArtifactType(int typeId, String guid, boolean isAbstract, String artifactTypeName) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public AttributeType createAttributeType(int typeId, String guid, boolean isAbstract, String artifactTypeName) {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public RelationType createRelationType(int typeId, String guid, boolean isAbstract, String artifactTypeName) {
      // TODO Auto-generated method stub
      return null;
   }
}
