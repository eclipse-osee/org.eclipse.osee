/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.util.internal;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.core.util.AbstractAtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsRelationChange;
import org.eclipse.osee.ats.core.util.AtsRelationChange.RelationOperation;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet extends AbstractAtsChangeSet {

   private SkynetTransaction transaction;

   public AtsChangeSet(String comment) {
      this(comment, AtsApiService.get().getAtsBranch(), AtsApiService.get().getUserService().getCurrentUser());
   }

   public AtsChangeSet(String comment, AtsUser asUser) {
      this(comment, AtsApiService.get().getAtsBranch(), asUser);
   }

   public AtsChangeSet(String comment, BranchToken branch, AtsUser asUser) {
      super(comment, branch, asUser);
   }

   private Artifact getArtifact(Object obj) {
      Artifact art = null;
      if (obj instanceof Artifact) {
         art = AtsApiService.get().getQueryServiceIde().getArtifact(obj);
      } else if (obj instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) obj;
         Object storeObject = atsObject.getStoreObject();
         if (storeObject != null) {
            art = getArtifact(storeObject);
         }
         if (art == null) {
            art = (Artifact) AtsApiService.get().getQueryService().getArtifact(atsObject.getId());
         }
      } else if (obj instanceof ArtifactId) {
         art = (Artifact) AtsApiService.get().getQueryService().getArtifact(((ArtifactId) obj).getId());
      }
      // If artifact can't be loaded, check that is't not a new artifact in this change set
      if (art == null && obj instanceof Id) {
         ArtifactId storedArt = getStoredArtifact((Id) obj);
         if (storedArt != null && storedArt instanceof Artifact) {
            art = (Artifact) storedArt;
         }
         if (art == null) {
            IAtsObject atsObject = getStoredAtsObject((Id) obj);
            if (atsObject != null && atsObject.getStoreObject() instanceof Artifact) {
               art = (Artifact) atsObject.getStoreObject();
            }
         }
      }
      return art;
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType) {
      checkExecuted();
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(workItem);
      artifact.deleteSoleAttribute(attributeType);
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, AttributeTypeToken attributeType, String value) {
      checkExecuted();
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(workItem);
      artifact.setSoleAttributeValue(attributeType, value);
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, Object value) {
      checkExecuted();
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(atsObject);
      artifact.setSoleAttributeValue(attributeType, value);
      add(artifact);
   }

   @Override
   public void addAttribute(IAtsObject atsObject, AttributeTypeToken attributeType, Object value) {
      checkExecuted();
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(atsObject);
      artifact.addAttribute(attributeType, value);
      add(artifact);
   }

   @Override
   public void deleteAttribute(IAtsObject atsObject, AttributeTypeToken attributeType, Object value) {
      checkExecuted();
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(atsObject);
      artifact.deleteAttribute(attributeType, value);
      add(artifact);
   }

   @Override
   public void deleteAttribute(ArtifactToken artifact, AttributeTypeToken attributeType, Object value) {
      checkExecuted();
      Artifact art = AtsApiService.get().getQueryServiceIde().getArtifact(artifact);
      art.deleteAttribute(attributeType, value);
      add(art);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<T> attr, AttributeTypeId attributeType, T value) {
      checkExecuted();
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(workItem);
      Attribute<T> attribute = (Attribute<T>) attr;
      attribute.setValue(value);
      add(artifact);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) {
      checkExecuted();
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(workItem);
      Attribute<?> attribute = (Attribute<?>) attr;
      attribute.delete();
      add(artifact);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(workItem);
      return artifact.getAttributeTypes().contains(attributeType);
   }

   @Override
   public void deleteAttributes(IAtsObject atsObject, AttributeTypeToken attributeType) {
      checkExecuted();
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(atsObject);
      artifact.deleteAttributes(attributeType);
      add(artifact);
   }

   @Override
   public ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name, Long artifactId) {
      checkExecuted();
      Artifact artifact = ArtifactTypeManager.addArtifact(artifactType, atsApi.getAtsBranch(), name, artifactId);
      add(artifact);
      return artifact;
   }

   @Override
   public ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name) {
      checkExecuted();
      Artifact artifact = ArtifactTypeManager.addArtifact(artifactType, atsApi.getAtsBranch(), name);
      add(artifact);
      return artifact;
   }

   @Override
   public void relate(Object object1, RelationTypeSide relationSide, Object object2) {
      checkExecuted();
      relatePrivate(object1, relationSide, object2);
   }

   @Override
   public void relate(ArtifactId artifact1, RelationTypeSide relationSide, ArtifactId artifact2) {
      checkExecuted();
      relatePrivate(artifact1, relationSide, artifact2);
   }

   public void relatePrivate(Object object1, RelationTypeSide relationSide, Object object2) {
      checkExecuted();
      Artifact artifact = getArtifact(object1);
      Artifact artifact2 = getArtifact(object2);
      artifact.addRelation(relationSide, artifact2);
      add(artifact);
      add(artifact2);
   }

   @Override
   public void unrelateAll(Object object, RelationTypeSide relationType) {
      checkExecuted();
      Artifact artifact = getArtifact(object);
      artifact.deleteRelations(relationType);
      add(artifact);
   }

   @Override
   public void setRelations(Object object, RelationTypeSide relationSide, Collection<? extends Object> objects) {
      checkExecuted();
      Artifact artifact = getArtifact(object);
      Set<Artifact> artifacts = new HashSet<>(objects.size());
      for (Object obj : objects) {
         Artifact art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
            add(art);
         }
      }
      artifact.setRelations(relationSide, artifacts);
      add(artifact);
   }

   @Override
   public void setRelationsAndOrder(Object object, RelationTypeSide relationSide,
      Collection<? extends Object> objects) {
      checkExecuted();
      Artifact artifact = getArtifact(object);
      Set<Artifact> artifacts = new HashSet<>(objects.size());
      for (Object obj : objects) {
         Artifact art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
            add(art);
         }
      }
      artifact.setRelations(RelationSorter.USER_DEFINED, relationSide, artifacts);
      add(artifact);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> void setAttribute(IAtsWorkItem workItem, AttributeId attributeId, T value) {
      checkExecuted();
      Conditions.checkExpressionFailOnTrue(attributeId.isInvalid(),
         "Can not set attribute by id that has not be persisted.  Attribute Id [%s] Work Item [%s]", attributeId,
         workItem.toStringWithId());
      Artifact artifact = getArtifact(workItem);
      Conditions.checkNotNull(artifact, "artifact");
      boolean found = false;
      for (Attribute<?> attribute : artifact.getAttributes()) {
         if (attributeId.equals(attribute)) {
            ((Attribute<T>) attribute).setValue(value);
            found = true;
            break;
         }
      }
      if (!found) {
         throw new OseeStateException("Attribute Id %s does not exist on Artifact %s", attributeId, workItem);
      }
      add(artifact);
   }

   @Override
   public void deleteArtifact(ArtifactId artifact) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      art.delete();
      add(art);
   }

   @Override
   public void setAttributeValues(IAtsObject atsObject, AttributeTypeToken attrType, List<Object> values) {
      checkExecuted();
      Artifact artifact = getArtifact(atsObject);
      artifact.setAttributeFromValues(attrType, values);
      add(artifact);
   }

   @Override
   public void setAttributeValues(ArtifactId artifact, AttributeTypeToken attrType, List<Object> values) {
      checkExecuted();
      AtsApiService.get().getQueryServiceIde().getArtifact(artifact).setAttributeFromValues(attrType, values);
      add(artifact);
   }

   @Override
   public <T> void setAttribute(ArtifactId artifact, AttributeId attrId, T value) {
      checkExecuted();
      Conditions.checkExpressionFailOnTrue(attrId.isInvalid(),
         "Can not set attribute by id that has not been persisted.  Attribute Id [%s] ArtifactId [%s]", attrId,
         artifact.toString());
      boolean found = false;
      for (Attribute<?> attribute : getArtifact(artifact).getAttributes()) {
         if (attrId.equals(attribute)) {
            attribute.setFromString(String.valueOf(value));
            found = true;
            break;
         }
      }
      if (!found) {
         throw new OseeStateException("Attribute Id %s does not exist on Artifact %s", attrId, artifact);
      }
      add(artifact);
   }

   @Override
   public void deleteAttribute(ArtifactId userArt, IAttribute<?> attr) {
      checkExecuted();
      Artifact artifact = getArtifact(userArt);
      artifact.deleteAttribute(attr);
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(ArtifactId artifact, AttributeTypeToken attrType, Object value) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      art.setSoleAttributeValue(attrType, value);
      add(artifact);
   }

   @Override
   public void unrelate(ArtifactId artifact, RelationTypeSide relationSide, ArtifactId artifact2) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      Artifact art2 = getArtifact(artifact2);
      art.deleteRelation(relationSide, art2);
      add(art);
      add(art2);
   }

   @Override
   public void addAttribute(ArtifactId artifact, AttributeTypeToken attrType, Object value) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      art.addAttribute(attrType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId artifact, AttributeTypeGeneric<?> attributeType, String value) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      art.setSoleAttributeFromString(attributeType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromStream(ArtifactId artifact, AttributeTypeGeneric<?> attributeType,
      InputStream inputStream) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      art.setSoleAttributeFromStream(attributeType, inputStream);
      add(art);
   }

   @Override
   public void unrelateFromAll(RelationTypeSide relationSide, ArtifactId artifact) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      art.deleteRelations(relationSide);
   }

   @Override
   public void addArtifactReferencedAttribute(ArtifactId artifact, AttributeTypeToken attributeType,
      ArtifactId artifactRef) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      art.addAttributeFromString(attributeType, artifactRef.getIdString());
   }

   @Override
   public void deleteAttributes(ArtifactId artifact, AttributeTypeToken attributeType) {
      checkExecuted();
      Artifact art = getArtifact(artifact);
      art.deleteAttributes(attributeType);
      add(art);
   }

   @Override
   public void setAttributeValuesAsStrings(IAtsObject atsObject, AttributeTypeToken attributeType,
      List<String> values) {
      checkExecuted();
      List<Object> objValues = new LinkedList<>();
      for (String value : values) {
         if (attributeType.isString() || attributeType.isEnumerated()) {
            try {
               objValues.add(value);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid date value [%s]; must be long date", value);
            }
         } else if (attributeType.isDate()) {
            try {
               Date date = new Date(Long.valueOf(value));
               objValues.add(date);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid date value [%s]; must be long date", value);
            }
         } else if (attributeType.isDouble()) {
            try {
               Double double1 = Double.valueOf(value);
               objValues.add(double1);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid double value [%s]", value);
            }
         } else if (attributeType.isInteger()) {
            try {
               Integer integer = Integer.valueOf(value);
               objValues.add(integer);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid integer value [%s]", value);
            }
         } else if (attributeType.isLong()) {
            try {
               Long longVal = Long.valueOf(value);
               objValues.add(longVal);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid long value [%s]", value);
            }
         } else if (attributeType.isBoolean()) {
            try {
               Boolean bool = Boolean.valueOf(value);
               objValues.add(bool);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid boolean value [%s]", value);
            }
         } else {
            throw new OseeArgumentException("Unsupported attribute value [%s] for type [%s]", attributeType, value);
         }
      }
      setAttributeValues(atsObject, attributeType, objValues);
   }

   @Override
   public ArtifactToken createArtifact(ArtifactToken parent, ArtifactTypeToken artType, String name) {
      checkExecuted();
      Artifact artifact = ArtifactTypeManager.addArtifact(artType, atsApi.getAtsBranch(), name);
      addChild(parent, artifact);
      add(artifact);
      return artifact;
   }

   @Override
   public void deleteRelation(RelationId relId) {
      checkExecuted();
      if (relId instanceof RelationLink) {
         RelationLink relation = (RelationLink) relId;
         SkynetTransaction transaction = TransactionManager.createTransaction(CoreBranches.COMMON, "Delete Relation");
         relation.delete(false, transaction);
         transaction.addArtifact(relation.getArtifactA());
         transaction.execute();
      }
   }

   @Override
   public void addTag(ArtifactToken artifact, String tag) {
      checkExecuted();
      if (!((Artifact) artifact).getTags().contains(tag)) {
         addAttribute(artifact, CoreAttributeTypes.StaticId, tag);
      }
   }

   /////////////////////////////////////////////
   ////////////// EXECUTE //////////////////////
   /////////////////////////////////////////////

   @Override
   protected void internalExecuteTransaction() {
      transaction = TransactionManager.createTransaction(branch, comment);
      for (IAtsObject atsObject : new ArrayList<>(atsObjects)) {
         transaction.addArtifact(AtsApiService.get().getQueryServiceIde().getArtifact(atsObject));
      }

      for (ArtifactId artifact : artifacts) {
         if (artifact instanceof Artifact) {
            transaction.addArtifact(AtsApiService.get().getQueryServiceIde().getArtifact(artifact));
         }
      }
      // Second, add or delete any relations; this has to be done separate so all artifacts are created
      for (AtsRelationChange rel : relations) {
         executeRelChange(rel, transaction);
      }
      // Third, delete any desired objects
      for (ArtifactId artifact : deleteArtifacts) {
         if (artifact instanceof Artifact) {
            if (!atsApi.getStoreService().isDeleted(artifact)) {
               AtsApiService.get().getQueryServiceIde().getArtifact(artifact).deleteAndPersist(transaction);
            }
         }
      }
      for (IAtsObject atsObject : deleteAtsObjects) {
         if (!AtsApiService.get().getStoreService().isDeleted(atsObject.getStoreObject())) {
            AtsApiService.get().getQueryServiceIde().getArtifact(atsObject).deleteAndPersist(transaction);
         }
      }
      TransactionToken transTok = transaction.execute();
      if (transTok != null) {
         transactionTok = transTok;
      }
   }

   @Override
   protected void executeHandleException(Exception ex) {
      transaction.cancel();
      super.executeHandleException(ex);
   }

   private void executeRelChange(AtsRelationChange relChange, SkynetTransaction transaction) {
      Conditions.checkNotNull(relChange, "relChange");
      Conditions.checkNotNull(relChange.getRelationSide(), "relationSide");
      Object obj = relChange.getObject();
      Artifact art = getArtifact(obj);
      Conditions.checkNotNull(art, "artifact");
      Collection<Object> objects = relChange.getObjects();
      Conditions.checkNotNullOrEmpty(objects, "objects");
      Set<Artifact> arts = new HashSet<>();
      for (Object obj2 : objects) {
         Artifact art2 = getArtifact(obj2);
         Conditions.checkNotNull(art2, "toArtifact");
         arts.add(art2);
      }
      for (Artifact artifact : arts) {
         List<Artifact> relatedArtifacts = art.getRelatedArtifacts(relChange.getRelationSide());
         if (relChange.getOperation() == RelationOperation.Add && !relatedArtifacts.contains(artifact)) {
            art.addRelation(relChange.getRelationSide(), artifact);
         } else if (relChange.getOperation() == RelationOperation.Delete && relatedArtifacts.contains(artifact)) {
            art.deleteRelation(relChange.getRelationSide(), artifact);
         }
      }
      art.persist(transaction);
   }

}