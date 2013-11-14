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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.ats.api.workflow.IAttribute;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.client.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet implements IAtsChangeSet {

   private String comment;
   private final Set<Object> objects = new HashSet<Object>();
   private final Set<Object> deleteObjects = new HashSet<Object>();
   private final Set<IExecuteListener> listeners = new HashSet<IExecuteListener>();

   public AtsChangeSet(String comment) {
      this.comment = comment;
   }

   @Override
   public void add(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      objects.add(obj);
   }

   @Override
   public void addAll(Object... objects) throws OseeCoreException {
      Conditions.checkNotNull(objects, "objects");
      for (Object obj : objects) {
         if (obj == null) {
            throw new OseeArgumentException("object can't be null");
         }
         this.objects.add(obj);
      }
   }

   @Override
   public void addToDelete(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      deleteObjects.add(obj);
   }

   @Override
   public void execute() throws OseeCoreException {
      Conditions.checkNotNull(comment, "comment");
      if (objects.isEmpty() && deleteObjects.isEmpty()) {
         throw new OseeArgumentException("objects/deleteObjects cannot be empty");
      }
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranchToken(), comment);
      for (Object obj : new CopyOnWriteArrayList<Object>(objects)) {
         if (obj instanceof IAtsWorkItem) {
            IAtsWorkItem workItem = (IAtsWorkItem) obj;
            if (workItem.getStateMgr().isDirty()) {
               AtsCore.getStateFactory().writeToStore(workItem, this);
            }
            if (workItem.getLog().isDirty()) {
               AtsCore.getLogFactory().writeToStore(workItem, AtsClientService.get().getAttributeResolver(), this);
            }
         }
      }
      for (Object obj : objects) {
         if (obj instanceof Artifact) {
            ((Artifact) obj).persist(transaction);
         } else {
            throw new OseeArgumentException("ATsChangeSet: Unhandled object type: " + obj);
         }
      }
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
      deleteObjects.clear();
      listeners.clear();
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

   public void reset(String comment) {
      clear();
      this.comment = comment;
   }

   @Override
   public boolean isEmpty() {
      return objects.isEmpty() && deleteObjects.isEmpty();
   }

   @Override
   public void addExecuteListener(IExecuteListener listener) {
      Conditions.checkNotNull(listener, "listener");
      listeners.add(listener);
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
   public void setSoleAttributeValue(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      artifact.setSoleAttributeValue(attributeType, value);
      add(artifact);
   }

   @Override
   public void addAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
      artifact.addAttribute(attributeType, value);
      add(artifact);
   }

   @Override
   public void deleteAttribute(IAtsWorkItem workItem, IAttributeType attributeType, Object value) throws OseeCoreException {
      Artifact artifact = AtsClientService.get().getArtifact(workItem);
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
}
