/*
 * Created on May 31, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Donald G. Dunne
 */
public class AtsConfigCache {

   // cache by guid and any other cachgeByTag item (like static id)
   private static final List<IAtsConfigObject> configObjects = new CopyOnWriteArrayList<IAtsConfigObject>();
   private static final HashCollection<String, IAtsConfigObject> tagToConfigObject =
      new HashCollection<String, IAtsConfigObject>(true, CopyOnWriteArrayList.class);

   public static void cache(IAtsConfigObject configObject) {
      configObjects.add(configObject);
      cacheByTag(configObject.getGuid(), configObject);
   }

   public static void cacheByTag(String tag, IAtsConfigObject configObject) {
      tagToConfigObject.put(tag, configObject);
   }

   @SuppressWarnings("unchecked")
   public static final <A extends IAtsConfigObject> List<A> getByTag(String tag, Class<A> clazz) {
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

   @SuppressWarnings("unchecked")
   public static final <A extends IAtsConfigObject> A getSoleByTag(String tag, Class<A> clazz) {
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

   @SuppressWarnings("unchecked")
   public static final <A extends IAtsConfigObject> A getSoleByName(String name, Class<A> clazz) {
      for (IAtsConfigObject obj : get(clazz)) {
         if (obj.getName().equals(name)) {
            return (A) obj;
         }
      }
      return null;
   }

   @SuppressWarnings("unchecked")
   public static final <A extends IAtsConfigObject> List<A> get(Class<A> clazz) {
      List<A> objs = new ArrayList<A>();
      for (IAtsConfigObject obj : configObjects) {
         if (clazz.isInstance(obj)) {
            objs.add((A) obj);
         }
      }
      return objs;
   }

   public static final <A extends IAtsConfigObject> A getSoleByGuid(String guid, Class<A> clazz) {
      if (guid.equals("BKbckZfNIkcx2wIVGkAA")) {
         System.out.println("where");
      }
      List<A> list = getByTag(guid, clazz);
      if (list.isEmpty()) {
         return null;
      }
      return list.iterator().next();
   }

   public static final IAtsConfigObject getSoleByGuid(String guid) {
      return getSoleByGuid(guid, IAtsConfigObject.class);
   }

   public static IAtsTeamDefinition getSoleByName(String teamDefName) {
      return null;
   }

   @SuppressWarnings("unchecked")
   public static final <A extends IAtsConfigObject> List<A> getByName(String name, Class<A> clazz) {
      List<A> objs = new ArrayList<A>();
      for (IAtsConfigObject obj : configObjects) {
         if (clazz.isInstance(obj) && obj.getName().equals(name)) {
            objs.add((A) obj);
         }
      }
      return objs;
   }

   public static void decache(IAtsConfigObject atsObject) {
      configObjects.remove(atsObject);
      List<String> keysToRemove = new ArrayList<String>();
      for (Entry<String, Collection<IAtsConfigObject>> entry : tagToConfigObject.entrySet()) {
         if (entry.getValue().contains(atsObject)) {
            keysToRemove.add(entry.getKey());
         }
      }
      for (String key : keysToRemove) {
         tagToConfigObject.removeValue(key, atsObject);
      }
   }

   public static void clearCaches() {
      tagToConfigObject.clear();
      configObjects.clear();
   }

}
