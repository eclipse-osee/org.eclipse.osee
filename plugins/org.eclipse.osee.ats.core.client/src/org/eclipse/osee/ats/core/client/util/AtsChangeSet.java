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
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class AtsChangeSet implements IAtsChangeSet {

   private String comment;
   private final Set<Object> objects = new HashSet<Object>();

   public AtsChangeSet(String comment) {
      this.comment = comment;
   }

   @Override
   public void add(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      objects.add(obj);
   }

   @Override
   public void execute() throws OseeCoreException {
      Conditions.checkNotNull(comment, "comment");
      Conditions.checkNotNullOrEmpty(objects, "objects");
      SkynetTransaction transaction = TransactionManager.createTransaction(AtsUtilCore.getAtsBranchToken(), comment);
      for (Object obj : objects) {
         if (obj instanceof Artifact) {
            ((Artifact) obj).persist(transaction);
         } else {
            throw new OseeArgumentException("Unhandled object type: " + obj);
         }
      }
      transaction.execute();
      objects.clear();
   }

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

   public boolean isEmpty() {
      return objects.isEmpty();
   }

}
