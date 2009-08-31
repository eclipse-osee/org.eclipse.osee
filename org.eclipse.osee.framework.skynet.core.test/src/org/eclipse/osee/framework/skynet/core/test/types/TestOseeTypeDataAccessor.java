package org.eclipse.osee.framework.skynet.core.test.types;

import java.util.Collection;
import java.util.Set;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.OseeEnumType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeDataAccessor;
import org.eclipse.osee.framework.skynet.core.types.IOseeTypeFactory;
import org.eclipse.osee.framework.skynet.core.types.OseeTypeCache;

public class TestOseeTypeDataAccessor implements IOseeTypeDataAccessor {
   private boolean loadAllArtifactTypes = false;
   private boolean loadAllAttributeTypes = false;
   private boolean loadAllOseeEnumTypes = false;
   private boolean loadAllRelationTypes = false;
   private boolean loadAllTypeValidity = false;
   private boolean storeArtifactType = false;
   private boolean storeAttributeType = false;
   private boolean storeRelationType = false;
   private boolean storeOseeEnumType = false;
   private boolean storeTypeInheritance = false;
   private boolean storeTypeValidity = false;

   public TestOseeTypeDataAccessor() {
   }

   public void setLoadAllArtifactTypes(boolean loadAllArtifactTypes) {
      this.loadAllArtifactTypes = loadAllArtifactTypes;
   }

   public void setLoadAllAttributeTypes(boolean loadAllAttributeTypes) {
      this.loadAllAttributeTypes = loadAllAttributeTypes;
   }

   public void setLoadAllOseeEnumTypes(boolean loadAllOseeEnumTypes) {
      this.loadAllOseeEnumTypes = loadAllOseeEnumTypes;
   }

   public void setLoadAllRelationTypes(boolean loadAllRelationTypes) {
      this.loadAllRelationTypes = loadAllRelationTypes;
   }

   public void setLoadAllTypeValidity(boolean loadAllTypeValidity) {
      this.loadAllTypeValidity = loadAllTypeValidity;
   }

   public void setStoreArtifactType(boolean storeArtifactType) {
      this.storeArtifactType = storeArtifactType;
   }

   public void setStoreAttributeType(boolean storeAttributeType) {
      this.storeAttributeType = storeAttributeType;
   }

   public void setStoreRelationType(boolean storeRelationType) {
      this.storeRelationType = storeRelationType;
   }

   public void setStoreTypeInheritance(boolean storeTypeInheritance) {
      this.storeTypeInheritance = storeTypeInheritance;
   }

   public void setStoreTypeValidity(boolean storeTypeValidity) {
      this.storeTypeValidity = storeTypeValidity;
   }

   public void setStoreOseeEnumType(boolean storeEnumType) {
      this.storeOseeEnumType = storeEnumType;
   }

   public boolean isLoadAllArtifactTypes() {
      return loadAllArtifactTypes;
   }

   public boolean isLoadAllAttributeTypes() {
      return loadAllAttributeTypes;
   }

   public boolean isLoadAllOseeEnumTypes() {
      return loadAllOseeEnumTypes;
   }

   public boolean isLoadAllRelationTypes() {
      return loadAllRelationTypes;
   }

   public boolean isLoadAllTypeValidity() {
      return loadAllTypeValidity;
   }

   public boolean isStoreOseeEnumType() {
      return storeOseeEnumType;
   }

   public boolean isStoreArtifactType() {
      return storeArtifactType;
   }

   public boolean isStoreAttributeType() {
      return storeAttributeType;
   }

   public boolean isStoreRelationType() {
      return storeRelationType;
   }

   public boolean isStoreTypeInheritance() {
      return storeTypeInheritance;
   }

   public boolean isStoreTypeValidity() {
      return storeTypeValidity;
   }

   @Override
   public void loadAllArtifactTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
      Assert.assertNotNull(cache);
      Assert.assertNotNull(artifactTypeFactory);
      setLoadAllArtifactTypes(true);
   }

   @Override
   public void loadAllAttributeTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
      Assert.assertNotNull(cache);
      Assert.assertNotNull(artifactTypeFactory);
      setLoadAllAttributeTypes(true);
   }

   @Override
   public void loadAllOseeEnumTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
      Assert.assertNotNull(cache);
      Assert.assertNotNull(artifactTypeFactory);
      setLoadAllOseeEnumTypes(true);
   }

   @Override
   public void loadAllRelationTypes(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
      Assert.assertNotNull(cache);
      Assert.assertNotNull(artifactTypeFactory);
      setLoadAllRelationTypes(true);
   }

   @Override
   public void loadAllTypeValidity(OseeTypeCache cache, IOseeTypeFactory artifactTypeFactory) throws OseeCoreException {
      Assert.assertNotNull(cache);
      Assert.assertNotNull(artifactTypeFactory);
      setLoadAllTypeValidity(true);
   }

   @Override
   public void storeArtifactType(Collection<ArtifactType> artifactType) throws OseeCoreException {
      Assert.assertNotNull(artifactType);
      setStoreArtifactType(true);
   }

   @Override
   public void storeAttributeType(Collection<AttributeType> attributeType) throws OseeCoreException {
      Assert.assertNotNull(attributeType);
      setStoreAttributeType(true);
   }

   @Override
   public void storeOseeEnumType(Collection<OseeEnumType> oseeEnumType) throws OseeCoreException {
      Assert.assertNotNull(oseeEnumType);
      setStoreOseeEnumType(true);
   }

   @Override
   public void storeRelationType(Collection<RelationType> relationType) throws OseeCoreException {
      Assert.assertNotNull(relationType);
      setStoreRelationType(true);
   }

   @Override
   public void storeTypeInheritance(ArtifactType artifactType, Set<ArtifactType> superTypes) throws OseeCoreException {
      Assert.assertNotNull(artifactType);
      Assert.assertNotNull(superTypes);
      setStoreTypeInheritance(true);
   }

   @Override
   public void storeValidity(CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>> validityData) throws OseeCoreException {
      Assert.assertNotNull(validityData);
      setStoreTypeValidity(true);
   }
}
