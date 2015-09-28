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
package org.eclipse.osee.ats.impl.internal.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.notify.IAtsNotifier;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.RuleEventType;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.util.AbstractAtsChangeSet;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.IAtsServer;
import org.eclipse.osee.ats.impl.util.WorkflowRuleRunner;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet extends AbstractAtsChangeSet {

   private TransactionBuilder transaction;

   private final IAttributeResolver attributeResolver;
   private final OrcsApi orcsApi;
   private final IAtsStateFactory stateFactory;
   private final IAtsLogFactory logFactory;
   private final IAtsNotifier notifier;

   private final IAtsServer atsServer;

   public AtsChangeSet(IAtsServer atsServer, IAttributeResolver attributeResolver, OrcsApi orcsApi, IAtsStateFactory stateFactory, IAtsLogFactory logFactory, String comment, IAtsUser user, IAtsNotifier notifier) {
      super(comment, user);
      this.atsServer = atsServer;
      this.attributeResolver = attributeResolver;
      this.orcsApi = orcsApi;
      this.stateFactory = stateFactory;
      this.logFactory = logFactory;
      this.notifier = notifier;
   }

   public TransactionBuilder getTransaction() throws OseeCoreException {
      if (transaction == null) {
         transaction =
            orcsApi.getTransactionFactory().createTransaction(AtsUtilCore.getAtsBranch(), getUser(user), comment);
      }
      return transaction;
   }

   private ArtifactReadable getUser(IAtsUser user) {
      if (user.getStoreObject() instanceof ArtifactReadable) {
         return (ArtifactReadable) user.getStoreObject();
      }
      return orcsApi.getQueryFactory().fromBranch(AtsUtilCore.getAtsBranch()).andUuid(
         user.getUuid()).getResults().getExactlyOne();
   }

   @Override
   public void execute() throws OseeCoreException {
      Conditions.checkNotNull(comment, "comment");
      if (objects.isEmpty() && deleteObjects.isEmpty()) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
      for (Object obj : objects) {
         if (obj instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) obj;
            IAtsStateManager stateMgr = workItem.getStateMgr();
            if (stateMgr.isDirty()) {
               stateFactory.writeToStore(user, workItem, this);
            }
            if (workItem.getLog().isDirty()) {
               logFactory.writeToStore(workItem, attributeResolver, this);
            }
         }
      }
      for (Object obj : deleteObjects) {
         if (obj instanceof IAtsWorkItem) {
            ArtifactReadable artifact = getArtifact(obj);
            getTransaction().deleteArtifact(artifact);
         } else {
            throw new OseeArgumentException("AtsChangeSet: Unhandled deleteObject type: " + obj);
         }
      }
      getTransaction().commit();
      for (IExecuteListener listener : listeners) {
         listener.changesStored(this);
      }
      notifier.sendNotifications(getNotifications());

      if (!workItemsCreated.isEmpty()) {
         WorkflowRuleRunner runner = new WorkflowRuleRunner(RuleEventType.CreateWorkflow, workItemsCreated, atsServer);
         runner.run();
      }
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
      getTransaction().deleteSoleAttribute(getArtifact(workItem), attributeType);
      add(workItem);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, String value) throws OseeCoreException {
      getTransaction().setSoleAttributeValue(getArtifact(workItem), attributeType, value);
      add(workItem);
   }

   @Override
   public void setSoleAttributeValue(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      getTransaction().setSoleAttributeValue(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public void deleteAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      getTransaction().deleteAttributesWithValue(getArtifact(atsObject), attributeType, value);
      add(atsObject);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, IAttributeType attributeType, T value) throws OseeCoreException {
      ArtifactId artifactId = getArtifact(workItem);
      getTransaction().setAttributeById(artifactId, new AttributeIdWrapper(attr), value);
      add(workItem);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException {
      getTransaction().deleteByAttributeId(getArtifact(workItem), new AttributeIdWrapper(attr));
      add(workItem);
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, IAttributeType attributeType) {
      ArtifactReadable artifact = getArtifact(workItem);
      return artifact.getValidAttributeTypes().contains(attributeType);
   }

   @Override
   public void addAttribute(IAtsObject atsObject, IAttributeType attributeType, Object value) throws OseeCoreException {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().createAttributeFromString(artifact, attributeType, String.valueOf(value));
      add(atsObject);
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name) {
      ArtifactId artifact = getTransaction().createArtifact(artifactType, name);
      add(artifact);
      return artifact;
   }

   @Override
   public void deleteAttributes(IAtsObject atsObject, IAttributeType attributeType) {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().deleteAttributes(artifact, attributeType);
      add(atsObject);
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name, String guid) {
      ArtifactId artifact = getTransaction().createArtifact(artifactType, name, guid);
      add(artifact);
      return artifact;
   }

   @Override
   public ArtifactId createArtifact(IArtifactType artifactType, String name, String guid, Long uuid) {
      ArtifactId artifact = getTransaction().createArtifact(artifactType, name, guid, uuid);
      add(artifact);
      return artifact;
   }

   @Override
   public void relate(Object object1, IRelationTypeSide relationSide, Object object2) {
      getTransaction().relate(getArtifact(object1), relationSide, getArtifact(object2));
      add(object1);
   }

   private ArtifactReadable getArtifact(Object object) {
      ArtifactReadable artifact = null;
      if (object instanceof ArtifactReadable) {
         artifact = (ArtifactReadable) object;
      } else if (object instanceof IAtsObject) {
         artifact = (ArtifactReadable) ((IAtsObject) object).getStoreObject();
      }
      return artifact;
   }

   @Override
   public void unrelateAll(Object object, IRelationTypeSide relationType) {
      ArtifactReadable artifact = getArtifact(object);
      getTransaction().unrelateFromAll(relationType, artifact);
      add(object);
   }

   @Override
   public void setRelation(Object object1, IRelationTypeSide relationType, Object object2) {
      unrelateAll(object1, relationType);
      relate(object1, relationType, object2);
      add(object1);
   }

   @Override
   public void setRelations(Object object, IRelationTypeSide relationSide, Collection<? extends Object> objects) {
      if (!relationSide.getSide().isSideA()) {
         throw new UnsupportedOperationException("Can only set relations from A to B side");
      }
      ArtifactReadable artifact = getArtifact(object);
      Set<ArtifactReadable> artifacts = new HashSet<>(objects.size());
      for (Object obj : objects) {
         ArtifactReadable art = getArtifact(obj);
         if (art != null) {
            artifacts.add(art);
         }
      }
      if (!relationSide.getSide().isSideA()) {
         getTransaction().setRelations(artifact, relationSide, artifacts);
      }
      add(object);
   }

   public void unrelate(Object object1, IRelationTypeSide relationType, Object object2) {
      getTransaction().unrelate(getArtifact(object1), relationType, getArtifact(object2));
      add(object1);
   }

   @Override
   public <T> void setAttribute(Object object, int attributeId, T value) {
      ArtifactReadable artifact = getArtifact(object);
      boolean found = false;
      for (AttributeReadable<Object> attribute : artifact.getAttributes()) {
         if (attribute.getGammaId() == attributeId) {
            getTransaction().setAttributeById(artifact, attribute, value);
            found = true;
            break;
         }
      }
      if (!found) {
         throw new OseeStateException("Attribute Id %d does not exist on Artifact %s", attributeId, object);
      }
      add(object);
   }
   
    @Override
   public void deleteArtifact(ArtifactId artifact) {
      getTransaction().deleteArtifact(artifact);
      add(artifact);
   }

   @Override
   public void setValues(IAtsObject atsObject, IAttributeType attrType, List<String> values) {
      ArtifactReadable artifact = getArtifact(atsObject);
      getTransaction().setAttributesFromValues(artifact, attrType, values);
      add(artifact);
   }

}
