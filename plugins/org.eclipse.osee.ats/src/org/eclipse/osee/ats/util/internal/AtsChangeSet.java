/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.internal;

import static org.eclipse.osee.framework.core.enums.RelationSorter.PREEXISTING;
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
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.util.AbstractAtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsRelationChange;
import org.eclipse.osee.ats.core.util.AtsRelationChange.RelationOperation;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.search.AtsArtifactQuery;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet extends AbstractAtsChangeSet {

   public AtsChangeSet(String comment) {
      this(comment, AtsClientService.get().getUserService().getCurrentUser());
   }

   public AtsChangeSet(String comment, IAtsUser asUser) {
      super(comment, asUser);
   }

   @Override
   public TransactionId execute() {
      Conditions.checkNotNull(comment, "comment");
      if (isEmpty() && execptionIfEmpty) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
      TransactionId transactionRecord;
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), comment);
      try {
         // First, create or update any artifacts that changed
         for (IAtsObject atsObject : new ArrayList<>(atsObjects)) {
            if (atsObject instanceof IAtsWorkItem) {
               IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
               IAtsStateManager stateMgr = workItem.getStateMgr();
               Conditions.assertNotNull(stateMgr, "StateManager");
               if (stateMgr.isDirty()) {
                  AtsClientService.get().getStateFactory().writeToStore(asUser, workItem, this);
               }
               if (workItem.getLog().isDirty()) {
                  AtsClientService.get().getLogFactory().writeToStore(workItem,
                     AtsClientService.get().getAttributeResolver(), this);
               }
            }
            transaction.addArtifact((Artifact) AtsClientService.get().getQueryService().getArtifact(atsObject));
         }
         for (ArtifactId artifact : artifacts) {
            if (artifact instanceof Artifact) {
               transaction.addArtifact((Artifact) artifact);
            }
         }
         // Second, add or delete any relations; this has to be done separate so all artifacts are created
         for (AtsRelationChange rel : relations) {
            execute(rel, transaction);
         }
         // Third, delete any desired objects
         for (ArtifactId artifact : deleteArtifacts) {
            if (artifact instanceof Artifact) {
               ((Artifact) artifact).deleteAndPersist(transaction);
            }
         }
         for (IAtsObject atsObject : deleteAtsObjects) {
            ((Artifact) AtsClientService.get().getQueryService().getArtifact(atsObject)).deleteAndPersist(transaction);
         }
         transactionRecord = transaction.execute();
      } catch (Exception ex) {
         transaction.cancel();
         throw OseeCoreException.wrap(ex);
      }
      for (IExecuteListener listener : listeners) {
         listener.changesStored(this);
      }
      AtsClientService.get().sendNotifications(getNotifications());
      for (IAtsObject atsObject : new ArrayList<>(atsObjects)) {
         if (atsObject instanceof IAtsWorkItem) {
            AtsClientService.get().getWorkDefinitionService().internalClearWorkDefinition((IAtsWorkItem) atsObject);
         }
      }

      /**
       * Commented out on 0.25.0 due to performance issues; No users are using this feature. Will be re-enabled on
       * 0.26.0 where analysis can be done and all action creation can be moved to the server. Same change in both
       * AtsChangeSets. See action TW1864.
       */
      //      if (!workItemsCreated.isEmpty()) {
      //         RunRuleData runRuleData = new RunRuleData();
      //         runRuleData.setRuleEventType(RuleEventType.CreateWorkflow);
      //         runRuleData.getWorkItemIds().addAll(AtsObjects.toIds(workItemsCreated));
      //         ElapsedTime time2 = new ElapsedTime("AtsChangeSet.runWorkflowRules");
      //         RunRuleResults results = AtsClientService.getRuleEp().runWorkflowRules(runRuleData);
      //         time2.end();
      //
      //         List<Artifact> changedArts = new LinkedList<>();
      //         for (Long changedId : results.getChangedWorkitemIds()) {
      //            Artifact artifact = ArtifactCache.getActive(changedId, AtsClientService.get().getAtsBranch());
      //            if (artifact != null) {
      //               changedArts.add(artifact);
      //            }
      //         }
      //         if (!changedArts.isEmpty()) {
      //            ArtifactQuery.reloadArtifacts(changedArts);
      //         }
      //      }
      return transactionRecord;
   }

   private void execute(AtsRelationChange relChange, SkynetTransaction transaction) {
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

   private Artifact getArtifact(Object obj) {
      Artifact art = null;
      if (obj instanceof Artifact) {
         art = (Artifact) obj;
      } else if (obj instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) obj;
         Object storeObject = atsObject.getStoreObject();
         if (storeObject != null) {
            art = getArtifact(storeObject);
         }
         if (art == null) {
            art = AtsArtifactQuery.getArtifactFromId(atsObject.getId());
         }
      }
      return art;
   }

   public static TransactionId execute(String comment, Object object, Object... objects) {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet(comment);
      changes.add(object);
      for (Object obj : objects) {
         changes.add(obj);
      }
      return changes.execute();
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(workItem);
      artifact.deleteSoleAttribute(attributeType);
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, AttributeTypeToken attributeType, String value) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(workItem);
      artifact.setSoleAttributeValue(attributeType, value);
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, Object value) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(atsObject);
      artifact.setSoleAttributeValue(attributeType, value);
      add(artifact);
   }

   @Override
   public void addAttribute(IAtsObject atsObject, AttributeTypeToken attributeType, Object value) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(atsObject);
      artifact.addAttribute(attributeType, value);
      add(artifact);
   }

   @Override
   public void deleteAttribute(IAtsObject atsObject, AttributeTypeId attributeType, Object value) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(atsObject);
      artifact.deleteAttribute(attributeType, value);
      add(artifact);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<T> attr, AttributeTypeId attributeType, T value) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(workItem);
      Attribute<T> attribute = (Attribute<T>) attr;
      attribute.setValue(value);
      add(artifact);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(workItem);
      Attribute<?> attribute = (Attribute<?>) attr;
      attribute.delete();
      add(artifact);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(workItem);
      return artifact.getAttributeTypes().contains(attributeType);
   }

   @Override
   public void deleteAttributes(IAtsObject atsObject, AttributeTypeId attributeType) {
      Artifact artifact = (Artifact) AtsClientService.get().getQueryService().getArtifact(atsObject);
      artifact.deleteAttributes(attributeType);
      add(artifact);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, Long artifactId) {
      Artifact artifact =
         ArtifactTypeManager.addArtifact(artifactType, AtsClientService.get().getAtsBranch(), name, artifactId);
      add(artifact);
      return artifact;
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name) {
      Artifact artifact = ArtifactTypeManager.addArtifact(artifactType, AtsClientService.get().getAtsBranch(), name);
      add(artifact);
      return artifact;
   }

   @Override
   public void relate(Object object1, RelationTypeSide relationSide, Object object2) {
      Artifact artifact = getArtifact(object1);
      Artifact artifact2 = getArtifact(object2);
      artifact.addRelation(relationSide, artifact2);
      add(artifact);
      add(artifact2);
   }

   @Override
   public void unrelateAll(Object object, RelationTypeSide relationType) {
      Artifact artifact = getArtifact(object);
      artifact.deleteRelations(relationType);
      add(artifact);
   }

   @Override
   public void setRelations(Object object, RelationTypeSide relationSide, Collection<? extends Object> objects) {
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
   public void setRelationsAndOrder(Object object, RelationTypeSide relationSide, Collection<? extends Object> objects) {
      Artifact artifact = getArtifact(object);
      Set<Artifact> artifacts = new HashSet<>(objects.size());
      for (Object obj : objects) {
         Artifact art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
            add(art);
         }
      }
      artifact.setRelations(PREEXISTING, relationSide, artifacts);
      add(artifact);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> void setAttribute(IAtsWorkItem workItem, AttributeId attributeId, T value) {
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
         throw new OseeStateException("Attribute Id %d does not exist on Artifact %s", attributeId, workItem);
      }
      add(artifact);
   }

   @Override
   public void deleteArtifact(ArtifactId artifact) {
      Artifact art = getArtifact(artifact);
      art.delete();
      add(art);
   }

   @Override
   public void setAttributeValues(IAtsObject atsObject, AttributeTypeToken attrType, List<Object> values) {
      Artifact artifact = getArtifact(atsObject);
      artifact.setAttributeFromValues(attrType, values);
      add(artifact);
   }

   @Override
   public void setAttributeValues(ArtifactId artifact, AttributeTypeToken attrType, List<Object> values) {
      ((Artifact) artifact).setAttributeFromValues(attrType, values);
      add(artifact);
   }

   @Override
   public <T> void setAttribute(ArtifactId artifact, AttributeId attrId, T value) {
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
      Artifact artifact = getArtifact(userArt);
      artifact.deleteAttribute(attr);
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(ArtifactId artifact, AttributeTypeToken attrType, Object value) {
      Artifact art = getArtifact(artifact);
      art.setSoleAttributeValue(attrType, value);
      add(artifact);
   }

   @Override
   public void unrelate(ArtifactId artifact, RelationTypeSide relationSide, ArtifactId artifact2) {
      Artifact art = getArtifact(artifact);
      Artifact art2 = getArtifact(artifact2);
      art.deleteRelation(relationSide, art2);
      add(art);
      add(art2);
   }

   @Override
   public void addAttribute(ArtifactId artifact, AttributeTypeToken attrType, Object value) {
      Artifact art = getArtifact(artifact);
      art.addAttribute(attrType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId artifact, AttributeTypeToken attrType, String value) {
      Artifact art = getArtifact(artifact);
      art.setSoleAttributeFromString(attrType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromStream(ArtifactId artifact, AttributeTypeToken attributeType, InputStream inputStream) {
      Artifact art = getArtifact(artifact);
      art.setSoleAttributeFromStream(attributeType, inputStream);
      add(art);
   }

   @Override
   public void unrelateFromAll(RelationTypeSide relationSide, ArtifactId artifact) {
      Artifact art = getArtifact(artifact);
      art.deleteRelations(relationSide);
   }

   @Override
   public void addArtifactReferencedAttribute(ArtifactId artifact, AttributeTypeToken attributeType, ArtifactId artifactRef) {
      Artifact art = getArtifact(artifact);
      art.addAttributeFromString(attributeType, artifactRef.getId().toString());
   }

   @Override
   public void deleteAttributes(ArtifactId artifact, AttributeTypeToken attributeType) {
      Artifact art = getArtifact(artifact);
      art.deleteAttributes(attributeType);
      add(art);
   }

   @Override
   public void setAttributeValuesAsStrings(IAtsObject atsObject, AttributeTypeToken attributeType, List<String> values) {
      List<Object> objValues = new LinkedList<>();
      for (String value : values) {
         if (AttributeTypeManager.isBaseTypeCompatible(StringAttribute.class, attributeType)) {
            try {
               objValues.add(value);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid date value [%v]; must be long date", value);
            }
         } else if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
            try {
               Date date = new Date(Long.valueOf(value));
               objValues.add(date);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid date value [%v]; must be long date", value);
            }
         } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeType)) {
            try {
               Double double1 = Double.valueOf(value);
               objValues.add(double1);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid double value [%v]", value);
            }
         } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeType)) {
            try {
               Integer integer = Integer.valueOf(value);
               objValues.add(integer);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid integer value [%v]", value);
            }
         } else if (AttributeTypeManager.isBaseTypeCompatible(LongAttribute.class, attributeType)) {
            try {
               Long longVal = Long.valueOf(value);
               objValues.add(longVal);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid long value [%v]", value);
            }
         } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeType)) {
            try {
               Boolean bool = Boolean.valueOf(value);
               objValues.add(bool);
            } catch (Exception ex) {
               throw new OseeArgumentException(ex, "Invalid long value [%v]", value);
            }
         } else {
            throw new OseeArgumentException("Unsupported attribute value [%v] for type [%s]", attributeType, value);
         }
      }
      setAttributeValues(getArtifact(atsObject), attributeType, objValues);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, Long artifactId, String guid) {
      Artifact artifact =
         ArtifactTypeManager.addArtifact(artifactType, AtsClientService.get().getAtsBranch(), name, guid, artifactId);
      add(artifact);
      return artifact;
   }

}