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
package org.eclipse.osee.ats.core.util;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IExecuteListener;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsChangeSet implements IAtsChangeSet {

   protected String comment;
   protected final Set<Object> objects = new CopyOnWriteArraySet<Object>();
   protected final Set<Object> deleteObjects = new CopyOnWriteArraySet<Object>();
   protected final Set<IExecuteListener> listeners = new CopyOnWriteArraySet<IExecuteListener>();
   protected final IAtsUser user;

   public AbstractAtsChangeSet(String comment, IAtsUser user) {
      this.comment = comment;
      this.user = user;
   }

   @Override
   public void add(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      if (obj instanceof Collection) {
         objects.addAll((Collection<?>) obj);
      } else {
         objects.add(obj);
      }
   }

   @Override
   public void addAll(Object... objects) throws OseeCoreException {
      Conditions.checkNotNull(objects, "objects");
      for (Object obj : objects) {
         add(obj);
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

   @Override
   public void addToDelete(Object obj) throws OseeCoreException {
      Conditions.checkNotNull(obj, "object");
      if (obj instanceof Collection) {
         deleteObjects.addAll((Collection<?>) obj);
      } else {
         deleteObjects.add(obj);
      }
   }

   public String getComment() {
      return comment;
   }

}
