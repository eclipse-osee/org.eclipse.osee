package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

public class OseeTypeCache {

   private final HashMap<String, RelationType> nameToRelationTypeMap = new HashMap<String, RelationType>();
   private final HashMap<Integer, RelationType> idToRelationTypeMap = new HashMap<Integer, RelationType>();

   private final HashMap<String, AttributeType> nameToAttributeTypeMap = new HashMap<String, AttributeType>();
   private final HashMap<Integer, AttributeType> idToToAttributeTypeMap = new HashMap<Integer, AttributeType>();

   private final HashMap<String, ArtifactType> nameToTypeMap = new HashMap<String, ArtifactType>();
   private final HashMap<Integer, ArtifactType> idToTypeMap = new HashMap<Integer, ArtifactType>();
   private final HashCollection<ArtifactType, ArtifactType> artifactTypeToSuperTypeMap;
   private final CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>> artifactToAttributeMap;

   public OseeTypeCache() {
      this.artifactTypeToSuperTypeMap = new HashCollection<ArtifactType, ArtifactType>();
      this.artifactToAttributeMap = new CompositeKeyHashMap<Branch, ArtifactType, Collection<AttributeType>>();
   }

   public void clearTypeValidity() {
      artifactToAttributeMap.clear();
   }

   public boolean isTypeValidityAvailable() {
      return !artifactToAttributeMap.isEmpty();
   }

   public boolean areArtifactTypesAvailable() {
      return !idToTypeMap.isEmpty();
   }

   public void cacheArtifactType(ArtifactType artifactType) {
      nameToTypeMap.put(artifactType.getName(), artifactType);
      idToTypeMap.put(artifactType.getArtTypeId(), artifactType);
   }

   public void cacheAttributeType(AttributeType attributeType) {
      nameToAttributeTypeMap.put(attributeType.getName(), attributeType);
      idToToAttributeTypeMap.put(attributeType.getAttrTypeId(), attributeType);
   }

   public void cacheRelationType(RelationType relationType) {
      nameToRelationTypeMap.put(relationType.getTypeName(), relationType);
      idToRelationTypeMap.put(relationType.getRelationTypeId(), relationType);
   }

   public ArtifactType getArtifactTypeById(int artTypeId) {
      return idToTypeMap.get(artTypeId);
   }

   public ArtifactType getArtifactTypeByName(String artifactTypeName) {
      return nameToTypeMap.get(artifactTypeName);
   }

   public void cacheArtifactTypeInheritance(ArtifactType artifactType, ArtifactType superType) {
      artifactTypeToSuperTypeMap.put(artifactType, superType);
   }

   public Collection<ArtifactType> getArtifactSuperType(ArtifactType artifactType) {
      Collection<ArtifactType> types = new HashSet<ArtifactType>();
      Collection<ArtifactType> stored = artifactTypeToSuperTypeMap.getValues(artifactType);
      if (stored != null) {
         types.addAll(stored);
      }
      return types;
   }

   public void cacheTypeValidity(ArtifactType artifactType, AttributeType attributeType, Branch branch) {
      Collection<AttributeType> attributeTypes = artifactToAttributeMap.get(branch, artifactType);
      if (attributeTypes == null) {
         attributeTypes = new HashSet<AttributeType>();
         artifactToAttributeMap.put(branch, artifactType, attributeTypes);
      }
      attributeTypes.add(attributeType);
   }

   public void cacheTypeValidity(ArtifactType artifactType, Collection<AttributeType> attributeTypes, Branch branch) {
      Collection<AttributeType> cachedItems = artifactToAttributeMap.get(branch, artifactType);
      if (cachedItems == null) {
         cachedItems = new HashSet<AttributeType>(attributeTypes);
         artifactToAttributeMap.put(branch, artifactType, cachedItems);
      } else {
         cachedItems.clear();
         cachedItems.addAll(attributeTypes);
      }
   }

   public Collection<ArtifactType> getAllArtifactTypes() {
      return new ArrayList<ArtifactType>(idToTypeMap.values());
   }

   public Collection<AttributeType> getAttributeTypes(ArtifactType artifactType) {
      Set<AttributeType> attributeTypes = new HashSet<AttributeType>();
      for (Entry<Pair<Branch, ArtifactType>, Collection<AttributeType>> entries : artifactToAttributeMap.entrySet()) {
         if (artifactType.equals(entries.getKey().getSecond())) {
            Collection<AttributeType> list = entries.getValue();
            if (list != null) {
               attributeTypes.addAll(list);
            }
         }
      }
      return attributeTypes;
   }

   public Collection<AttributeType> getAttributeTypes(ArtifactType artifactType, Branch branch) {
      return artifactToAttributeMap.get(branch, artifactType);
   }
}
