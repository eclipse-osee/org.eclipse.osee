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

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.ats.core.client.search.AtsArtifactQuery;
import org.eclipse.osee.ats.core.util.AbstractAtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsRelationChange;
import org.eclipse.osee.ats.core.util.AtsRelationChange.RelationOperation;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet extends AbstractAtsChangeSet {

   public AtsChangeSet(String comment) {
      super(comment, AtsClientService.get().getUserService().getCurrentUser());
   }

   @Override
   public void execute() throws OseeCoreException {
      Conditions.checkNotNull(comment, "comment");
      if (objects.isEmpty() && deleteObjects.isEmpty()) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), comment);
      // First, create or update any artifacts that changed
      for (Object obj : objects) {
         if (obj instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) obj;
            if (workItem.getStateMgr().isDirty()) {
               AtsClientService.get().getStateFactory().writeToStore(user, workItem, this);
               ((Artifact) workItem.getStoreObject()).persist(transaction);
            }
            if (workItem.getLog().isDirty()) {
               AtsClientService.get().getLogFactory().writeToStore(workItem,
                  AtsClientService.get().getAttributeResolver(), this);
               ((Artifact) workItem.getStoreObject()).persist(transaction);
            }
         } else if (obj instanceof IAtsConfigObject) {
            IAtsConfigObject configObj = (IAtsConfigObject) obj;
            Artifact storeConfigObject = AtsClientService.get().storeConfigObject(configObj, this);
            storeConfigObject.persist(transaction);
         }
         if (obj instanceof Artifact) {
            ((Artifact) obj).persist(transaction);
         } else if (obj instanceof IAtsObject && ((IAtsObject) obj).getStoreObject() instanceof Artifact) {
            ((Artifact) ((IAtsObject) obj).getStoreObject()).persist(transaction);
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
      transaction.execute();
      for (IExecuteListener listener : listeners) {
         listener.changesStored(this);
      }
      AtsClientService.get().sendNotifications(getNotifications());
   }

   private void execute(AtsRelationChange relChange, SkynetTransaction transaction) {
      Conditions.checkNotNull(relChange, "relChange");
      Conditions.checkNotNull(relChange.getRelationSide(), "relationSide");
      Object obj = relChange.getObject();
      Artifact art = getArtifact(obj);
      Conditions.checkNotNull(art, "artifact");
      Collection<Object> objects = relChange.getObjects();
      Conditions.checkNotNullOrEmpty(objects, "objects");
      Set<Artifact> arts = new HashSet<Artifact>();
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

   public static void execute(String comment, Object object, Object... objects) throws OseeCoreException {
      AtsChangeSet changes = new AtsChangeSet(comment);
      changes.add(object);
      for (Object obj : objects) {
         changes.add(obj);
      }
      changes.execute();
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
      getArtifact(object1).addRelation(relationSide, getArtifact(object2));
   }

   @Override
   public void unrelateAll(Object object, IRelationTypeSide relationType) {
      getArtifact(object).deleteRelations(relationType);
   }

   @Override
   public void setRelation(Object object1, IRelationTypeSide relationType, Object object2) {
      Artifact artifact = getArtifact(object1);
      Artifact artifact2 = getArtifact(object2);
      artifact.setRelations(relationType, Collections.singleton(artifact2));
   }

   @Override
   public void setRelations(Object object, IRelationTypeSide relationSide, Collection<? extends Object> objects) {
      Artifact artifact = getArtifact(object);
      Set<Artifact> artifacts = new HashSet<Artifact>(objects.size());
      for (Object obj : objects) {
         Artifact art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
         }
      }
      artifact.setRelations(null, relationSide, artifacts);
   }

   @SuppressWarnings("unchecked")
   @Override
   public <T> void setAttribute(Object object, int attributeId, T value) {
      Artifact artifact = getArtifact(object);
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
         throw new OseeStateException("Attribute Id %d does not exist on Artifact %s", attributeId, object);
      }
      add(artifact);
   }
}
