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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateManager;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.impl.internal.AtsServerService;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactId;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet implements IAtsChangeSet {

   private String comment;
   private final Set<Object> objects = new HashSet<Object>();
   private final Set<Object> deleteObjects = new HashSet<Object>();
   private TransactionBuilder transaction;
   private final Set<IExecuteListener> listeners = new HashSet<IExecuteListener>();
   private final IAtsUser user;

   public AtsChangeSet(String comment, IAtsUser user) {
      this.comment = comment;
      this.user = user;
   }

   @Override
   public void add(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      objects.add(obj);
   }

   public TransactionBuilder getTransaction() throws OseeCoreException {
      if (transaction == null) {
         transaction =
            AtsServerService.get().getOrcsApi().getTransactionFactory(null).createTransaction(
               AtsUtilServer.getAtsBranch(), getUser(user), comment);
      }
      return transaction;
   }

   private ArtifactReadable getUser(IAtsUser user) {
      if (user.getStoreObject() instanceof ArtifactReadable) {
         return (ArtifactReadable) user.getStoreObject();
      }
      return AtsServerService.get().getOrcsApi().getQueryFactory(null).fromBranch(AtsUtilServer.getAtsBranch()).andGuid(
         user.getGuid()).getResults().getExactlyOne();
   }

   public IAtsStateManager getStateMgr(IAtsWorkItem workItem) {
      return workItem.getStateMgr();
   }

   @Override
   public void execute() throws OseeCoreException {
      Conditions.checkNotNull(comment, "comment");
      if (objects.isEmpty() && deleteObjects.isEmpty()) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
      for (Object obj : new CopyOnWriteArrayList<Object>(objects)) {
         if (obj instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) obj;
            IAtsStateManager stateMgr = getStateMgr(workItem);
            if (stateMgr.isDirty()) {
               AtsCore.getStateFactory().writeToStore(workItem, this);
            }
            if (workItem.getLog().isDirty()) {
               AtsCore.getLogFactory().writeToStore(workItem, AtsCore.getAttrResolver(), this);
            }
         }
      }
      for (Object obj : deleteObjects) {
         if (obj instanceof IAtsWorkItem) {
            ArtifactReadable artifact = AtsServerService.get().getArtifact((IAtsWorkItem) obj);
            getTransaction().deleteArtifact(artifact);
         } else {
            throw new OseeArgumentException("AtsChangeSet: Unhandled deleteObject type: " + obj);
         }
      }
      getTransaction().commit();
      for (IExecuteListener listener : listeners) {
         listener.changesStored(this);
      }
   }

   public static void execute(String comment, IAtsUser user, Object object, Object... objects) throws OseeCoreException {
      AtsChangeSet changes = new AtsChangeSet(comment, user);
      changes.add(object);
      for (Object obj : objects) {
         changes.add(obj);
      }
      changes.execute();
   }

   @Override
   public Set<Object> getObjects() {
      return objects;
   }

   public void setComment(String comment) {
      this.comment = comment;
   }

   @Override
   public void clear() {
      objects.clear();
   }

   public void reset(String comment) {
      clear();
      this.comment = comment;
      this.transaction = null;
      this.objects.clear();
   }

   @Override
   public boolean isEmpty() {
      return objects.isEmpty();
   }

   @Override
   public void deleteSoleAttribute(IAtsWorkItem workItem, IAttributeType attributeType) throws OseeCoreException {
      getTransaction().deleteSoleAttribute(AtsUtilCore.toArtifactId(workItem), attributeType);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, String value) throws OseeCoreException {
      getTransaction().setSoleAttributeValue(AtsUtilCore.toArtifactId(workItem), attributeType, value);
   }

   @Override
   public void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException {
      getTransaction().setSoleAttributeValue(AtsUtilCore.toArtifactId(workItem), attributeType, value);
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException {
      getTransaction().createAttribute(AtsUtilCore.toArtifactId(workItem), attributeType, value);
   }

   @Override
   public void deleteAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException {
      getTransaction().deleteAttributesWithValue(AtsUtilCore.toArtifactId(workItem), attributeType, value);
   }

   @Override
   public <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, IAttributeType attributeType, T value) throws OseeCoreException {
      ArtifactId artifactId = AtsUtilCore.toArtifactId(workItem);
      getTransaction().setAttributeById(artifactId, AtsUtilCore.toAttributeId(attr), value);
   }

   @Override
   public <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr) throws OseeCoreException {
      getTransaction().deleteByAttributeId(AtsUtilCore.toArtifactId(workItem), AtsUtilCore.toAttributeId(attr));
   }

   @Override
   public void addExecuteListener(IExecuteListener listener) {
      listeners.add(listener);
   }

   @Override
   public void addToDelete(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      deleteObjects.add(obj);
   }

   @Override
   public void addAll(Object... objects) throws OseeCoreException {
      for (Object obj : objects) {
         this.objects.add(obj);
      }
   }

   @Override
   public boolean isAttributeTypeValid(IAtsWorkItem workItem, IAttributeType attributeType) {
      ArtifactReadable artifact = AtsServerService.get().getArtifact(workItem);
      return artifact.getValidAttributeTypes().contains(attributeType);
   }

}
