/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigCache implements IAtsConfig {

   // cache by guid and any other cachgeByTag item (like static id)
   private final List<IAtsConfigObject> configObjects = new CopyOnWriteArrayList<IAtsConfigObject>();
   private final HashCollection<String, IAtsConfigObject> tagToConfigObject =
      new HashCollection<String, IAtsConfigObject>(true, CopyOnWriteArrayList.class);
   private final HashCollection<Long, IAtsConfigObject> idToConfigObject =
      new HashCollection<Long, IAtsConfigObject>(true, CopyOnWriteArrayList.class);

   public void cache(IAtsConfigObject configObject) {
      Conditions.checkNotNull(configObject, "configObject");
      configObjects.add(configObject);
      cacheById(configObject.getUuid(), configObject);
   }

   public void cacheByTag(String tag, IAtsConfigObject configObject) {
      Conditions.checkNotNull(tag, "tag");
      Conditions.checkNotNull(configObject, "configObject");
      tagToConfigObject.put(tag, configObject);
   }

   public void cacheById(long id, IAtsConfigObject configObject) {
      Conditions.checkNotNull(configObject, "configObject");
      idToConfigObject.put(id, configObject);
   }

   public void invalidateByUuid(long uuid) {
      idToConfigObject.removeValues(uuid);
   }

   /**
    * Clear out all values cached by tag and add sole tag to this configObject
    */
   public void cacheSoleByTag(String tag, IAtsConfigObject configObject) {
      Conditions.checkNotNull(tag, "tag");
      Conditions.checkNotNull(configObject, "configObject");
      Collection<IAtsConfigObject> values = tagToConfigObject.getValues(tag);
      if (values != null) {
         values.clear();
      }
      cacheByTag(tag, configObject);
   }

   @Override
   @SuppressWarnings("unchecked")
   public final <A extends IAtsConfigObject> List<A> getByTag(String tag, Class<A> clazz) {
      Conditions.checkNotNull(tag, "tag");
      List<A> objs = new ArrayList<A>();
      Collection<IAtsConfigObject> values = tagToConfigObject.getValues(tag);
      if (values != null) {
         for (IAtsConfigObject obj : values) {
            if (clazz.isInstance(obj)) {
               objs.add((A) obj);
            }
         }
      }
      return objs;
   }

   @Override
   @SuppressWarnings("unchecked")
   public final <A extends IAtsConfigObject> List<A> getById(long id, Class<A> clazz) {
      List<A> objs = new ArrayList<A>();
      Collection<IAtsConfigObject> values = idToConfigObject.getValues(id);
      if (values != null) {
         for (IAtsConfigObject obj : values) {
            if (clazz.isInstance(obj)) {
               objs.add((A) obj);
            }
         }
      }
      return objs;
   }

   @Override
   @SuppressWarnings("unchecked")
   public final <A extends IAtsConfigObject> A getSoleByTag(String tag, Class<A> clazz) {
      Conditions.checkNotNull(tag, "tag");
      Collection<IAtsConfigObject> values = tagToConfigObject.getValues(tag);
      if (values != null) {
         for (IAtsConfigObject obj : values) {
            if (clazz.isInstance(obj)) {
               return (A) obj;
            }
         }
      }
      return null;
   }

   @Override
   @SuppressWarnings("unchecked")
   public final <A extends IAtsConfigObject> List<A> get(Class<A> clazz) {
      List<A> objs = new ArrayList<A>();
      for (IAtsConfigObject obj : configObjects) {
         if (clazz.isInstance(obj)) {
            objs.add((A) obj);
         }
      }
      return objs;
   }

   @Override
   public void getReport(XResultData rd) {
      rd.logWithFormat("TagToConfigObject size %d\n", tagToConfigObject.keySet().size());
      rd.logWithFormat("ConfigObjects size %d\n", configObjects.size());
   }

   @Override
   public String toString() {
      return configObjects.toString();
   }

   @Override
   public void invalidate(IAtsConfigObject atsObject) {
      Conditions.checkNotNull(atsObject, "atsObject");
      configObjects.remove(atsObject);
      List<String> keysToRemove = new ArrayList<String>();
      for (Entry<String, Collection<IAtsConfigObject>> entry : tagToConfigObject.entrySet()) {
         if (entry.getValue().contains(atsObject)) {
            keysToRemove.add(entry.getKey());
         }
      }
      invalidateByUuid(atsObject.getUuid());
      for (String key : keysToRemove) {
         tagToConfigObject.removeValue(key, atsObject);
      }
   }

   public void invalidateByTag(String tag) {
      Conditions.checkNotNull(tag, "tag");
      tagToConfigObject.removeValues(tag);
   }

   @Override
   public <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz) throws OseeCoreException {
      List<A> list = getById(uuid, clazz);
      if (list.isEmpty()) {
         return null;
      }
      return list.iterator().next();
   }

   @Override
   public IAtsConfigObject getSoleByUuid(long uuid) throws OseeCoreException {
      return getSoleByUuid(uuid, IAtsConfigObject.class);
   }

}
