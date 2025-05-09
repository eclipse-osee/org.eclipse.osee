/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.rest.internal.util;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.core.util.AbstractAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet extends AbstractAtsChangeSet {

   private TransactionBuilder transaction;
   private final OrcsApi orcsApi;
   private final AtsApi atsApi;

   public AtsChangeSet(AtsApi atsApi, IAttributeResolver attributeResolver, OrcsApi orcsApi, IAtsLogFactory logFactory, String comment, AtsUser user, BranchToken branch) {
      super(comment, branch, user);
      this.atsApi = atsApi;
      this.orcsApi = orcsApi;
   }

   public TransactionBuilder getTransaction() {
      if (transaction == null) {
         if (branch == null) {
            branch = atsApi.getAtsBranch();
         }
         if (asUser == null) {
            transaction = orcsApi.getTransactionFactory().createTransaction(branch, comment);
         } else {
            transaction = orcsApi.getTransactionFactory().createTransaction(branch, asUser, comment);
         }
      }
      return transaction;
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType) {
      checkExecuted();
      getTransaction().deleteSoleAttribute(getArtifact(workItem), attributeType);
      add(workItem);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, AttributeTypeToken attributeType, String value) {
      checkExecuted();
      ArtifactReadable artifact = getArtifact(workItem);
      setSoleAttributeValue(artifact, attributeType, value);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, Object value) {
      checkExecuted();
      getTransaction().setSoleAttributeValue(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public void deleteAttribute(IAtsObject atsObject, AttributeTypeToken attributeType, Object value) {
      checkExecuted();
      getTransaction().deleteAttributesWithValue(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public void deleteAttribute(ArtifactToken artifact, AttributeTypeToken attributeType, Object value) {
      checkExecuted();
      getTransaction().deleteAttributesWithValue(getArtifact(artifact), attributeType, value);
      add(artifact);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<T> attr, AttributeTypeId attributeType, T value) {
      checkExecuted();
      ArtifactId artifactId = getArtifact(workItem);
      getTransaction().setAttributeById(artifactId, attr, value);
      add(workItem);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) {
      checkExecuted();
      getTransaction().deleteByAttributeId(getArtifact(workItem), attr);
      add(workItem);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      ArtifactReadable artifact = getArtifact(workItem);
      return artifact.getValidAttributeTypes().contains(attributeType);
   }

   @Override
   public void addAttribute(IAtsObject atsObject, AttributeTypeToken attributeType, Object value) {
      checkExecuted();
      getTransaction().createAttribute(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public ArtifactToken createArtifact(ArtifactTypeToken artType, BranchToken branch, String name) {
      checkExecuted();
      ArtifactToken artifact = getTransaction().createArtifact(artType, name);
      add(artifact);
      return artifact;
   }

   @Override
   public ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name) {
      checkExecuted();
      ArtifactToken artifact = getTransaction().createArtifact(artifactType, name);
      add(artifact);
      return artifact;
   }

   @Override
   public ArtifactToken createArtifact(ArtifactTypeToken artifactType, String name, Long artifactId) {
      checkExecuted();
      ArtifactToken artifact = getTransaction().createArtifact(artifactType, name, ArtifactId.valueOf(artifactId));
      add(artifact);
      return artifact;
   }

   @Override
   public void deleteAttributes(IAtsObject atsObject, AttributeTypeToken attributeType) {
      checkExecuted();
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().deleteAttributes(artifact, attributeType);
      add(atsObject);
   }

   @Override
   public void relate(Object object1, RelationTypeSide relationSide, Object object2) {
      checkExecuted();
      ArtifactId artifact = getArtifact(object1);
      ArtifactId artifact2 = getArtifact(object2);
      relate(artifact, relationSide, artifact2);
   }

   @Override
   public void relate(ArtifactId artifact1, RelationTypeSide relationSide, ArtifactId artifact2) {
      checkExecuted();
      if (relationSide.getSide().isSideA()) {
         getTransaction().relate(artifact2, relationSide, artifact1);
      } else {
         getTransaction().relate(artifact1, relationSide, artifact2);
      }
      add(artifact1);
      add(artifact2);
   }

   private ArtifactReadable getArtifact(Object object) {
      ArtifactReadable artifact = null;
      if (object instanceof ArtifactReadable) {
         artifact = (ArtifactReadable) object;
      }

      // Get from change set if already created/loaded/used
      if (artifact == null && object instanceof Id) {
         ArtifactId storedArt = getStoredArtifact((Id) object);
         if (storedArt != null && storedArt instanceof ArtifactReadable) {
            artifact = (ArtifactReadable) storedArt;
         }
         if (artifact == null) {
            IAtsObject atsObject = getStoredAtsObject((Id) object);
            if (atsObject != null && atsObject.getStoreObject() instanceof ArtifactReadable) {
               artifact = (ArtifactReadable) atsObject.getStoreObject();
            }
         }
      }

      if (artifact == null) {
         if (object instanceof IAtsObject) {
            IAtsObject atsObject = (IAtsObject) object;
            if (atsObject.getStoreObject() instanceof ArtifactReadable) {
               artifact = (ArtifactReadable) atsObject.getStoreObject();
            } else {
               artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(atsObject.getId());
            }
         } else if (object instanceof ArtifactId) {
            artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(((ArtifactId) object).getId());
         }
      }
      return artifact;
   }

   @Override
   public void unrelateAll(Object object, RelationTypeSide relationType) {
      checkExecuted();
      ArtifactReadable artifact = getArtifact(object);
      add(artifact);
      for (ArtifactReadable otherArt : artifact.getRelated(relationType)) {
         if (relationType.getSide().isSideA()) {
            getTransaction().unrelate(otherArt, relationType, artifact);
         } else {
            getTransaction().unrelate(artifact, relationType, otherArt);
         }
         add(otherArt);
      }
   }

   @Override
   public void setRelationsAndOrder(Object object, RelationTypeSide relationSide,
      Collection<? extends Object> objects) {
      checkExecuted();
      ArtifactReadable artifact = getArtifact(object);
      List<ArtifactReadable> artifacts = new LinkedList<>();
      for (Object obj : objects) {
         ArtifactReadable art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
         }
      }

      getTransaction().setRelationsAndOrder(artifact, relationSide, artifacts);
      artifacts.add(artifact);
   }

   @Override
   public void setRelations(Object object, RelationTypeSide relationSide, Collection<? extends Object> objects) {
      checkExecuted();
      ArtifactReadable artifact = getArtifact(object);
      List<ArtifactReadable> artifacts = new LinkedList<>();
      for (Object obj : objects) {
         ArtifactReadable art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
         }
      }

      // unrelate all objects that are not in set
      for (ArtifactReadable art : artifact.getRelated(relationSide)) {
         if (!artifacts.contains(art)) {
            unrelate(artifact, relationSide, art);
         }
      }

      // add all relations that do not exist
      for (Object obj : objects) {
         ArtifactReadable art = getArtifact(obj);
         if (!artifact.areRelated(relationSide, art)) {
            relate(object, relationSide, obj);
         }
      }

   }

   public void unrelate(Object object1, RelationTypeSide relationType, Object object2) {
      checkExecuted();
      getTransaction().unrelate(getArtifact(object1), relationType, getArtifact(object2));
      add(object1);
   }

   @Override
   public <T> void setAttribute(IAtsWorkItem workItem, AttributeId attributeId, T value) {
      checkExecuted();
      Conditions.checkExpressionFailOnTrue(attributeId.isInvalid(),
         "Can not set attribute by id that has not be persisted.  Attribute Id [%s] Work Item [%s]", attributeId,
         workItem.toStringWithId());
      ArtifactReadable artifact = getArtifact(workItem);
      boolean found = false;
      for (AttributeReadable<Object> attribute : artifact.getAttributes()) {
         if (attributeId.equals(attribute)) {
            getTransaction().setAttributeById(artifact, attribute, value);
            found = true;
            break;
         }
      }
      if (!found) {
         throw new OseeStateException("Attribute Id %d does not exist on Artifact %s", attributeId, workItem);
      }
      add(workItem);
   }

   @Override
   public void deleteArtifact(ArtifactId artifact) {
      checkExecuted();
      if (!atsApi.getStoreService().isDeleted(artifact)) {
         getTransaction().deleteArtifact(artifact);
         add(artifact);
      }
   }

   @Override
   public void setAttributeValues(IAtsObject atsObject, AttributeTypeToken attrType, List<Object> values) {
      checkExecuted();
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().setAttributesFromValues(artifact, attrType, values);
      add(artifact);
   }

   @Override
   public void setAttributeValues(ArtifactId artifact, AttributeTypeToken attrType, List<Object> values) {
      checkExecuted();
      getTransaction().setAttributesFromValues(artifact, attrType, values);
      add(artifact);
   }

   @Override
   public <T> void setAttribute(ArtifactId artifact, AttributeId attrId, T value) {
      checkExecuted();
      Conditions.checkExpressionFailOnTrue(attrId.isInvalid(),
         "Can not set attribute by id that has not been persisted.  Atrribute Id [%s] ArtifactId [%s]", attrId,
         artifact.toString());
      ArtifactReadable artifact2 = getArtifact(artifact);
      for (AttributeReadable<?> attribute : artifact2.getAttributes()) {
         if (attrId.equals(attribute)) {
            getTransaction().setAttributeById(artifact2, attribute, value);
            add(artifact2);
         }
      }
   }

   @Override
   public void setSoleAttributeValue(ArtifactId artifact, AttributeTypeToken attrType, Object value) {
      checkExecuted();
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().setSoleAttributeValue(art, attrType, value);
      add(art);
   }

   @Override
   public void deleteAttribute(ArtifactId artifact, IAttribute<?> attr) {
      checkExecuted();
      AttributeId attribute = ((ArtifactReadable) artifact).getAttributeById(attr);
      getTransaction().deleteByAttributeId(artifact, attribute);
      add(artifact);
   }

   @Override
   public void unrelate(ArtifactId artifact, RelationTypeSide relationSide, ArtifactId artifact2) {
      checkExecuted();
      ArtifactReadable art = getArtifact(artifact);
      ArtifactReadable art2 = getArtifact(artifact2);
      if (relationSide.getSide().isSideA()) {
         getTransaction().unrelate(art2, relationSide, art);
      } else {
         getTransaction().unrelate(art, relationSide, art2);
      }
      add(art);
      add(art2);
   }

   @Override
   public void addAttribute(ArtifactId artifact, AttributeTypeToken attrType, Object value) {
      checkExecuted();
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().createAttribute(artifact, attrType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId artifact, AttributeTypeGeneric<?> attributeType, String value) {
      checkExecuted();
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().setSoleAttributeFromString(artifact, attributeType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromStream(ArtifactId artifact, AttributeTypeGeneric<?> attributeType,
      InputStream inputStream) {
      checkExecuted();
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().setSoleAttributeFromStream(art, attributeType, inputStream);
      add(art);
   }

   @Override
   public void unrelateFromAll(RelationTypeSide relationSide, ArtifactId artifact) {
      checkExecuted();
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().unrelateFromAll(relationSide, art);
      add(art);
   }

   @Override
   public void deleteAttributes(ArtifactId artifact, AttributeTypeToken attributeType) {
      checkExecuted();
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().deleteAttributes(art, attributeType);
      add(art);
   }

   @Override
   public void setAttributeValuesAsStrings(IAtsObject atsObject, AttributeTypeToken attributeType,
      List<String> values) {
      checkExecuted();
      List<Object> objValues = new LinkedList<>();
      for (String value : values) {
         if (attributeType.isString()) {
            try {
               objValues.add(value);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid value [%s]; must be string", value);
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
         } else if (attributeType.isEnumerated()) {
            try {
               objValues.add(value);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid value [%s]; must be string", value);
            }
         } else {
            throw new OseeArgumentException("Unsupported attribute value [%s] for type [%s] and artifact %s", value,
               attributeType, atsObject.toStringWithId());
         }
      }
      ArtifactReadable art = getArtifact(atsObject);
      setAttributeValues(art, attributeType, objValues);
   }

   @Override
   public void addArtifactReferencedAttribute(ArtifactId artifact, AttributeTypeToken attributeType,
      ArtifactId artifactRef) {
      checkExecuted();
      addAttribute(artifact, attributeType, artifactRef.getIdString());
   }

   @Override
   public ArtifactToken createArtifact(ArtifactToken parent, ArtifactTypeToken artType, String name) {
      checkExecuted();
      ArtifactToken artifact = getTransaction().createArtifact(artType, name);
      addChild(parent, artifact);
      add(artifact);
      return artifact;
   }

   @Override
   public void deleteRelation(RelationId relation) {
      checkExecuted();
      throw new UnsupportedOperationException("Unsupported on Server");
   }

   @Override
   public void addTag(ArtifactToken artifact, String tag) {
      checkExecuted();
      if (!((ArtifactReadable) artifact).getTags().contains(tag)) {
         addAttribute(artifact, CoreAttributeTypes.StaticId, tag);
      }
   }

   /////////////////////////////////////////////
   ////////////// EXECUTE //////////////////////
   /////////////////////////////////////////////

   @Override
   protected void internalExecuteTransaction() {
      transactionTok = getTransaction().commit();
   }

}