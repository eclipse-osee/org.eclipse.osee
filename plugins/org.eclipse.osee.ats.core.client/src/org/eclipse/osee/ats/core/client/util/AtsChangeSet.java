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
package org.eclipse.osee.ats.core.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workdef.RunRuleData;
import org.eclipse.osee.ats.api.workdef.RunRuleResults;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.util.AbstractAtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.core.util.AtsRelationChange;
import org.eclipse.osee.ats.core.util.AtsRelationChange.RelationOperation;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
   public TransactionId execute() throws OseeCoreException {
      Conditions.checkNotNull(comment, "comment");
      if (objects.isEmpty() && deleteObjects.isEmpty() && execptionIfEmpty) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), comment);
      // First, create or update any artifacts that changed
      for (Object obj : new ArrayList<>(objects)) {
         if (obj instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) obj;
            if (workItem.getStateMgr().isDirty()) {
               AtsClientService.get().getStateFactory().writeToStore(asUser, workItem, this);
            }
            if (workItem.getLog().isDirty()) {
               AtsClientService.get().getLogFactory().writeToStore(workItem,
                  AtsClientService.get().getAttributeResolver(), this);
            }
         }

         if (obj instanceof Artifact) {
            transaction.addArtifact((Artifact) obj);
         } else if (obj instanceof IAtsObject && ((IAtsObject) obj).getStoreObject() instanceof Artifact) {
            transaction.addArtifact((Artifact) ((IAtsObject) obj).getStoreObject());
         }
      }
      // Second, add or delete any relations; this has to be done separate so all artifacts are created
      for (Object obj : objects) {
         if (obj instanceof AtsRelationChange) {
            execute((AtsRelationChange) obj, transaction);
         }
      }
      // Third, delete any desired objects
      for (Object obj : deleteObjects) {
         if (obj instanceof Artifact) {
            ((Artifact) obj).deleteAndPersist(transaction);
         } else {
            throw new OseeArgumentException("ATsChangeSet: Unhandled deleteObject type: " + obj);
         }
      }
      TransactionId transactionRecord = transaction.execute();
      for (IExecuteListener listener : listeners) {
         listener.changesStored(this);
      }
      AtsClientService.get().sendNotifications(getNotifications());

      if (!workItemsCreated.isEmpty()) {
         RunRuleData runRuleData = new RunRuleData();
         runRuleData.setRuleEventType(RuleEventType.CreateWorkflow);
         runRuleData.getWorkItemUuids().addAll(AtsObjects.toUuids(workItemsCreated));
         RunRuleResults results = AtsClientService.getRuleEp().runWorkflowRules(runRuleData);

         List<Artifact> changedArts = new LinkedList<>();
         for (Long changedUuid : results.getChangedWorkitemUuids()) {
            Artifact artifact = ArtifactCache.getActive(changedUuid.intValue(), AtsUtilCore.getAtsBranch());
            if (artifact != null) {
               changedArts.add(artifact);
            }
         }
         if (!changedArts.isEmpty()) {
            ArtifactQuery.reloadArtifacts(changedArts);
         }
      }
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
            art = AtsArtifactQuery.getArtifactFromId(atsObject.getUuid());
         }
      }
      return art;
   }

   public void addTo(SkynetTransaction transaction) throws OseeCoreException {
      Conditions.checkNotNull(transaction, "transaction");
      for (Object obj : objects) {
         if (obj instanceof Artifact) {
            ((Artifact) obj).persist(transaction);
         } else {
            throw new OseeArgumentException("Unhandled object type");
         }
      }
   }

   public static TransactionId execute(String comment, Object object, Object... objects) throws OseeCoreException {
      AtsChangeSet changes = new AtsChangeSet(comment);
      changes.add(object);
      for (Object obj : objects) {
         changes.add(obj);
      }
      return changes.execute();
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      artifact.deleteSoleAttribute(attributeType);
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, String value) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      artifact.setSoleAttributeValue(attributeType, value);
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(atsObject);
      artifact.setSoleAttributeValue(attributeType, value);
      add(artifact);
   }

   @Override
   public void addAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(atsObject);
      artifact.addAttribute(attributeType, value);
      add(artifact);
   }

   @Override
   public void deleteAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(atsObject);
      artifact.deleteAttribute(attributeType, value);
      add(artifact);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, IAttributeType attributeType, T value) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      @SuppressWarnings("unchecked")
      Attribute<T> attribute = (Attribute<T>) attr.getData();
      attribute.setValue(value);
      add(artifact);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      Attribute<?> attribute = (Attribute<?>) attr.getData();
      attribute.delete();
      add(artifact);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, IAttributeType attributeType) {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      return artifact.getAttributeTypes().contains(attributeType);
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name) {
      Artifact artifact = ArtifactTypeManager.addArtifact(artifactType, AtsUtilCore.getAtsBranch(), name);
      add(artifact);
      return artifact;
   }

   @Override
   public void deleteAttributes(IAtsObject atsObject, IAttributeType attributeType) {
      Artifact artifact = AtsClientService.get().getArtifact(atsObject);
      artifact.delete();
      add(artifact);
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name, String guid) {
      return createArtifact(artifactType, name, guid, Lib.generateArtifactIdAsInt());
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name, String guid, Long uuid) {
      Artifact artifact = ArtifactTypeManager.addArtifact(artifactType, AtsUtilCore.getAtsBranch(), name, guid, uuid);
      add(artifact);
      return artifact;
   }

   @Override
   public void relate(Object object1, IRelationTypeSide relationSide, Object object2) {
      Artifact artifact = getArtifact(object1);
      Artifact artifact2 = getArtifact(object2);
      artifact.addRelation(relationSide, artifact2);
      add(artifact);
      add(artifact2);
   }

   @Override
   public void unrelateAll(Object object, IRelationTypeSide relationType) {
      Artifact artifact = getArtifact(object);
      artifact.deleteRelations(relationType);
      add(artifact);
   }

   @Override
   public void setRelations(Object object, IRelationTypeSide relationSide, Collection<? extends Object> objects) {
      Artifact artifact = getArtifact(object);
      Set<Artifact> artifacts = new HashSet<>(objects.size());
      for (Object obj : objects) {
         Artifact art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
            add(art);
         }
      }
      artifact.setRelations(RelationOrderBaseTypes.PREEXISTING, relationSide, artifacts);
      add(artifact);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> void setAttribute(IAtsWorkItem workItem, int attributeId, T value) {
      Artifact artifact = getArtifact(workItem);
      Conditions.checkNotNull(artifact, "artifact");
      boolean found = false;
      for (Attribute<?> attribute : artifact.getAttributes()) {
         if (attribute.getId() == attributeId) {
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
   public void setValues(IAtsObject atsObject, IAttributeType attrType, List<String> values) {
      Artifact artifact = getArtifact(atsObject);
      artifact.setAttributeValues(attrType, values);
      add(artifact);
   }

   @Override
   public <T> void setAttribute(ArtifactId artifact, int attrId, T value) {
      boolean found = false;
      for (Attribute<?> attribute : getArtifact(artifact).getAttributes()) {
         if (attribute.getId() == attrId) {
            attribute.setFromString(String.valueOf(value));
            found = true;
            break;
         }
      }
      if (!found) {
         throw new OseeStateException("Attribute Id %d does not exist on Artifact %s", attrId, artifact);
      }
      add(artifact);
   }

   @Override
   public void deleteAttribute(ArtifactId userArt, IAttribute<?> attr) {
      Artifact artifact = getArtifact(userArt);
      artifact.deleteAttribute(attr.getId());
      add(artifact);
   }

   @Override
   public void setSoleAttributeValue(ArtifactId artifact, IAttributeType attrType, String value) {
      Artifact art = getArtifact(artifact);
      art.setSoleAttributeValue(attrType, value);
      add(artifact);
   }

   @Override
   public void unrelate(ArtifactId artifact, IRelationTypeSide relationSide, ArtifactId artifact2) {
      Artifact art = getArtifact(artifact);
      Artifact art2 = getArtifact(artifact2);
      art.deleteRelation(relationSide, art2);
      add(art);
      add(art2);
   }

   @Override
   public void addAttribute(ArtifactId artifact, IAttributeType attrType, Object value) {
      Artifact art = getArtifact(artifact);
      art.addAttribute(attrType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId artifact, IAttributeType attrType, String value) {
      Artifact art = getArtifact(artifact);
      art.setSoleAttributeFromString(attrType, value);
      add(art);
   }

}
