/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.core.util.AbstractAtsChangeSet;
import org.eclipse.osee.ats.rest.IAtsServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet extends AbstractAtsChangeSet {

   private TransactionBuilder transaction;

   private final OrcsApi orcsApi;
   private final IAtsNotifier notifier;

   private final IAtsServer atsServer;

   public AtsChangeSet(IAtsServer atsServer, IAttributeResolver attributeResolver, OrcsApi orcsApi, IAtsStateFactory stateFactory, IAtsLogFactory logFactory, String comment, IAtsUser user, IAtsNotifier notifier) {
      super(comment, user);
      this.atsServer = atsServer;
      this.orcsApi = orcsApi;
      this.notifier = notifier;
   }

   public TransactionBuilder getTransaction() throws OseeCoreException {
      if (transaction == null) {
         transaction =
            orcsApi.getTransactionFactory().createTransaction(atsServer.getAtsBranch(), getUser(asUser), comment);
      }
      return transaction;
   }

   private ArtifactReadable getUser(IAtsUser user) {
      if (user.getStoreObject() instanceof ArtifactReadable) {
         return (ArtifactReadable) user.getStoreObject();
      }
      return orcsApi.getQueryFactory().fromBranch(atsServer.getAtsBranch()).andUuid(
         user.getId()).getResults().getExactlyOne();
   }

   @Override
   public TransactionId execute() throws OseeCoreException {
      Conditions.checkNotNull(comment, "comment");
      if (isEmpty() && execptionIfEmpty) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
      // First, create or update any artifacts that changed
      for (IAtsObject atsObject : new ArrayList<>(atsObjects)) {
         if (atsObject instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) atsObject;
            if (workItem.getStateMgr().isDirty()) {
               atsServer.getStateFactory().writeToStore(asUser, workItem, this);
            }
            if (workItem.getLog().isDirty()) {
               atsServer.getLogFactory().writeToStore(workItem, atsServer.getAttributeResolver(), this);
            }
         }
      }
      TransactionReadable transactionReadable = getTransaction().commit();
      for (IExecuteListener listener : listeners) {
         listener.changesStored(this);
      }
      notifier.sendNotifications(getNotifications());
      for (IAtsObject atsObject : new ArrayList<>(atsObjects)) {
         if (atsObject instanceof IAtsWorkItem) {
            atsServer.getStoreService().clearCaches((IAtsWorkItem) atsObject);
         }
      }

      /**
       * Commented out on 0.25.0 due to performance issues; No users are using this feature. Will be re-enabled on
       * 0.26.0 where analysis can be done and all action creation can be moved to the server. Same change in both
       * AtsChangeSets. See action TW1864.
       */
      //      if (!workItemsCreated.isEmpty()) {
      //         WorkflowRuleRunner runner = new WorkflowRuleRunner(RuleEventType.CreateWorkflow, workItemsCreated, atsServer);
      //         runner.run();
      //      }
      return transactionReadable;
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType) throws OseeCoreException {
      getTransaction().deleteSoleAttribute(getArtifact(workItem), attributeType);
      add(workItem);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, AttributeTypeId attributeType, String value) throws OseeCoreException {
      ArtifactReadable artifact = getArtifact(workItem);
      setSoleAttributeValue(artifact, attributeType, value);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value) throws OseeCoreException {
      getTransaction().setSoleAttributeValue(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public void deleteAttribute(IAtsObject atsObject, AttributeTypeId attributeType, Object value) throws OseeCoreException {
      getTransaction().deleteAttributesWithValue(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, AttributeTypeId attributeType, T value) throws OseeCoreException {
      ArtifactId artifactId = getArtifact(workItem);
      getTransaction().setAttributeById(artifactId, attr, value);
      add(workItem);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException {
      getTransaction().deleteByAttributeId(getArtifact(workItem), attr);
      add(workItem);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeId attributeType) {
      ArtifactReadable artifact = getArtifact(workItem);
      return artifact.getValidAttributeTypes().contains(attributeType);
   }

   @Override
   public void addAttribute(IAtsObject atsObject, AttributeTypeId attributeType, Object value) throws OseeCoreException {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().createAttribute(artifact, attributeType, value);
      add(atsObject);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name) {
      ArtifactToken artifact = getTransaction().createArtifact(artifactType, name);
      add(artifact);
      return artifact;
   }

   @Override
   public void deleteAttributes(IAtsObject atsObject, AttributeTypeId attributeType) {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().deleteAttributes(artifact, attributeType);
      add(atsObject);
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, String guid) {
      ArtifactToken artifact = getTransaction().createArtifact(artifactType, name, guid);
      add(artifact);
      return artifact;
   }

   @Override
   public ArtifactToken createArtifact(IArtifactType artifactType, String name, String guid, Long uuid) {
      ArtifactToken artifact = getTransaction().createArtifact(artifactType, name, guid, uuid);
      add(artifact);
      return artifact;
   }

   @Override
   public void relate(Object object1, RelationTypeSide relationSide, Object object2) {
      ArtifactId artifact = getArtifact(object1);
      ArtifactId artifact2 = getArtifact(object2);
      if (relationSide.getSide().isSideA()) {
         getTransaction().relate(artifact2, relationSide, artifact);
      } else {
         getTransaction().relate(artifact, relationSide, artifact2);
      }
      add(artifact);
      add(artifact2);
   }

   private ArtifactReadable getArtifact(Object object) {
      ArtifactReadable artifact = null;
      if (object instanceof ArtifactReadable) {
         artifact = (ArtifactReadable) object;
      } else if (object instanceof IAtsObject) {
         IAtsObject atsObject = (IAtsObject) object;
         if (atsObject.getStoreObject() instanceof ArtifactReadable) {
            artifact = (ArtifactReadable) atsObject.getStoreObject();
         } else {
            artifact = atsServer.getArtifact(atsObject.getId());
         }
      }
      return artifact;
   }

   @Override
   public void unrelateAll(Object object, RelationTypeSide relationType) {
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
   public void setRelations(Object object, RelationTypeSide relationSide, Collection<? extends Object> objects) {
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
         if (!art.areRelated(relationSide, art)) {
            relate(object, relationSide, obj);
         }
      }

   }

   public void unrelate(Object object1, RelationTypeSide relationType, Object object2) {
      getTransaction().unrelate(getArtifact(object1), relationType, getArtifact(object2));
      add(object1);
   }

   @Override
   public <T> void setAttribute(IAtsWorkItem workItem, int attributeId, T value) {
      Conditions.checkExpressionFailOnTrue(attributeId <= 0,
         "Can not set attribute by id that has not be persisted.  Atrribute Id [%s] Work Item [%s]", attributeId,
         workItem.toStringWithId());
      ArtifactReadable artifact = getArtifact(workItem);
      boolean found = false;
      for (AttributeReadable<Object> attribute : artifact.getAttributes()) {
         if (attribute.getGammaId() == attributeId) {
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
      getTransaction().deleteArtifact(artifact);
      add(artifact);
   }

   @Override
   public void setAttributeValues(IAtsObject atsObject, AttributeTypeId attrType, List<Object> values) {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().setAttributesFromValues(artifact, attrType, values);
      add(artifact);
   }

   @Override
   public void setAttributeValues(ArtifactId artifact, AttributeTypeId attrType, List<Object> values) {
      getTransaction().setAttributesFromValues(artifact, attrType, values);
      add(artifact);
   }

   @Override
   public <T> void setAttribute(ArtifactId artifact, AttributeId attrId, T value) {
      Conditions.checkExpressionFailOnTrue(attrId.isInvalid(),
         "Can not set attribute by id that has not been persisted.  Atrribute Id [%s] ArtifactId [%s]", attrId,
         artifact.toString());
      for (AttributeReadable<?> attribute : getArtifact(artifact).getAttributes()) {
         if (attrId.equals(attribute)) {
            getTransaction().setAttributeById(getArtifact(artifact), attribute, value);
         }
      }
   }

   @Override
   public void setSoleAttributeValue(ArtifactId artifact, AttributeTypeId attrType, Object value) {
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().setSoleAttributeValue(art, attrType, value);
      add(art);
   }

   @Override
   public void deleteAttribute(ArtifactId artifact, IAttribute<?> attr) {
      AttributeId attribute = ((ArtifactReadable) artifact).getAttributeById(attr);
      getTransaction().deleteByAttributeId(artifact, attribute);
      add(artifact);
   }

   @Override
   public void unrelate(ArtifactId artifact, RelationTypeSide relationSide, ArtifactId artifact2) {
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
   public void addAttribute(ArtifactId artifact, AttributeTypeId attrType, Object value) {
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().createAttribute(artifact, attrType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromString(ArtifactId artifact, AttributeTypeId attrType, String value) {
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().setSoleAttributeFromString(artifact, attrType, value);
      add(art);
   }

   @Override
   public void setSoleAttributeFromStream(ArtifactId artifact, AttributeTypeId attributeType, InputStream inputStream) {
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().setSoleAttributeFromStream(art, attributeType, inputStream);
      add(art);
   }

   @Override
   public void unrelateFromAll(RelationTypeSide relationSide, ArtifactId artifact) {
      ArtifactReadable art = getArtifact(artifact);
      getTransaction().unrelateFromAll(relationSide, art);
      add(art);
   }

   @Override
   public void addArtifactReferencedAttribute(ArtifactId artifact, AttributeTypeId attributeType, ArtifactId artifactRef) {
      addAttribute(artifact, attributeType, artifactRef.getId().toString());
   }

}
