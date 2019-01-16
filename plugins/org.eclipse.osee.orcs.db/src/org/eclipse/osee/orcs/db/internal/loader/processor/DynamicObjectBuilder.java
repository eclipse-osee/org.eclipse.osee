/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.processor;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import org.eclipse.osee.framework.core.enums.ObjectType;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.DynamicData;
import org.eclipse.osee.orcs.core.ds.DynamicDataHandler;
import org.eclipse.osee.orcs.core.ds.DynamicObject;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.ResultObjectDescription;
import org.eclipse.osee.orcs.db.internal.sql.ObjectField;
import org.eclipse.osee.orcs.db.internal.sql.SqlFieldResolver;

/**
 * @author Roberto E. Escobar
 */
public class DynamicObjectBuilder {

   private final Log logger;
   private final OrcsTypes orcsTypes;
   private final Options options;
   private final Stack<ObjectMap> stack = new Stack<>();

   private DynamicDataHandler handler;
   private Iterable<? extends DynamicData> descriptors;
   private Boolean showHidden;
   private ObjectMap rootObject;

   public DynamicObjectBuilder(Log logger, OrcsTypes orcsTypes, Options options) {
      super();
      this.logger = logger;
      this.orcsTypes = orcsTypes;
      this.options = options;
   }

   public void setHandler(DynamicDataHandler handler) {
      this.handler = handler;
   }

   private boolean showHiddenFields() {
      if (showHidden == null) {
         showHidden = OptionsUtil.showHiddenFields(options);
      }
      return showHidden;
   }

   public Iterable<? extends DynamicData> getDescriptors() {
      if (descriptors == null) {
         descriptors = options.getObject(ResultObjectDescription.class, "result.descriptor").getDynamicData();
      }
      return descriptors;
   }

   private ObjectMap pop() {
      return !stack.isEmpty() ? stack.pop() : null;
   }

   private ObjectMap peek() {
      return !stack.isEmpty() ? stack.peek() : null;
   }

   public void onDynamicObjectStart(DynamicObject data) {
      logger.trace("DynamicObject - start - [%s]", data);
      ObjectMap object = new ObjectMap();
      object.setData(data);
      stack.push(object);
   }

   public void onDynamicField(DynamicData data, String fieldName, Object value) {
      logger.trace("DynamicObject - field - [%s] - field:[%s] value:[%s]", data, fieldName, value);
      ObjectMap current = stack.peek();
      if (data.isPrimaryKey()) {
         current.addHash(value);
      }
      if (isTypeField(data)) {
         current.setType(value);
      }
      if (showHiddenFields() || !data.isHidden()) {
         current.addData(fieldName, value);
      }
   }

   private boolean isTypeField(DynamicData data) {
      ObjectField objectField = SqlFieldResolver.getObjectField(data);
      return objectField != null ? objectField.isMetaTypeField() : false;
   }

   public void onDynamicObjectEnd(DynamicObject data) {
      logger.trace("DynamicObject - end - [%s]", data);
      ObjectMap current = pop();
      if (current != null) {
         Long hashCode = current.getHash();
         ObjectMap parent = peek();
         if (parent != null) {
            parent.addData(data.getName(), current);
         }
         if (stack.isEmpty()) {
            if (rootObject != null) {
               Long rootHash = rootObject.getHash();
               if (!rootHash.equals(hashCode)) {
                  handler.onDynamicData(rootObject.asMap());
                  rootObject = current;
               } else {
                  rootObject.merge(current);
               }
            } else {
               rootObject = current;
            }
         }
      }
   }

   public void onEnd() {
      if (rootObject != null) {
         handler.onDynamicData(rootObject.asMap());
      }
   }

   private final class ObjectMap {
      private DynamicObject descriptor;
      private Long hash = 37L;
      private final Map<String, Object> data = new LinkedHashMap<>();
      private Map<Long, ObjectMap> children;
      private Long typeId;

      public void addData(String key, Object value) {
         if (value instanceof ObjectMap) {
            if (children == null) {
               children = Maps.newLinkedHashMap();
            }
            ObjectMap child = (ObjectMap) value;
            children.put(child.getHash(), child);
         } else {
            data.put(key, value);
         }
      }

      public DynamicObject getDescriptor() {
         return descriptor;
      }

      public void setData(DynamicObject descriptor) {
         this.descriptor = descriptor;
      }

      public void setType(Object value) {
         typeId = Long.parseLong(String.valueOf(value));
      }

      public Long getType() {
         return typeId;
      }

      public void merge(ObjectMap other) {
         data.putAll(other.data);
         this.typeId = other.typeId;
         if (other.hasChildren()) {
            mergeChildren(other.children.entrySet());
         }
      }

      private void mergeChildren(Iterable<Entry<Long, ObjectMap>> otherChildren) {
         for (Entry<Long, ObjectMap> otherChild : otherChildren) {
            Long key = otherChild.getKey();
            ObjectMap value = otherChild.getValue();

            ObjectMap thisChild = children.get(key);
            if (thisChild != null) {
               thisChild.merge(value);
            } else {
               children.put(key, value);
            }
         }
      }

      public boolean hasChildren() {
         return children != null && !children.isEmpty();
      }

      public void addHash(Object value) {
         hash = hash * Long.parseLong(String.valueOf(value));
      }

      public Long getHash() {
         return hash;
      }

      public Map<String, Object> asMap() {
         if (hasChildren()) {
            for (Entry<String, Collection<ObjectMap>> entries : getChildrenByName()) {
               String key = entries.getKey();
               Collection<ObjectMap> values = entries.getValue();
               ObjectMap object = Iterables.getFirst(values, null);
               ObjectType objectType = getObjectType(object.getDescriptor());
               if (ObjectType.ATTRIBUTE == objectType || ObjectType.RELATION == objectType) {
                  data.put(key, groupByTypeName(objectType, values));
               } else {
                  data.put(key, asSetMap(values));
               }
            }
         }
         return data;
      }

      private Map<String, Object> groupByTypeName(ObjectType objectType, Collection<ObjectMap> values) {
         SetMultimap<String, Map<String, Object>> byTypeName = newSetMultimap();
         for (ObjectMap child : values) {
            String typeName = resolveTypeName(objectType, child.getType());
            byTypeName.put(typeName, child.asMap());
         }

         Map<String, Object> toReturn = new LinkedHashMap<>();
         for (Entry<String, Collection<Map<String, Object>>> entry : byTypeName.asMap().entrySet()) {
            Collection<Map<String, Object>> collection = entry.getValue();
            if (collection.size() == 1) {
               toReturn.put(entry.getKey(), collection.iterator().next());
            } else {
               toReturn.put(entry.getKey(), collection);
            }
         }
         return toReturn;
      }

      private Iterable<Entry<String, Collection<ObjectMap>>> getChildrenByName() {
         SetMultimap<String, ObjectMap> objectMaps = newSetMultimap();
         for (ObjectMap child : children.values()) {
            DynamicObject descriptor = child.getDescriptor();
            String name = descriptor.getName();
            objectMaps.put(name, child);
         }
         return objectMaps.asMap().entrySet();
      }

      private Set<Map<String, Object>> asSetMap(Collection<ObjectMap> values) {
         Set<Map<String, Object>> toReturn = new LinkedHashSet<>();
         for (ObjectMap child : values) {
            toReturn.add(child.asMap());
         }
         return toReturn;
      }

      public ObjectType getObjectType(DynamicObject descriptor) {
         String type = descriptor.getGuid();
         return ObjectField.objectType(type);
      }

      public String resolveTypeName(ObjectType parentType, Long type) {
         String typeName;
         Named typeObject = null;
         switch (parentType) {
            case ARTIFACT:
               typeObject = orcsTypes.getArtifactTypes().get(type);
               break;
            case ATTRIBUTE:
               typeObject = orcsTypes.getAttributeTypes().get(type);
               break;
            case RELATION:
               typeObject = orcsTypes.getRelationTypes().get(type);
               break;
            default:
               break;
         }

         if (typeObject != null) {
            typeName = typeObject.getName();
         } else {
            typeName = String.valueOf(type);
         }
         return typeName;
      }

      @Override
      public String toString() {
         return "ObjectMap [hash=" + hash + ", typeId=" + typeId + ", data=" + data + ", children=" + children + "]";
      }
   }

   private static <K, V> SetMultimap<K, V> newSetMultimap() {
      Map<K, Collection<V>> map = Maps.newLinkedHashMap();
      return Multimaps.newSetMultimap(map, new Supplier<Set<V>>() {
         @Override
         public Set<V> get() {
            return Sets.newLinkedHashSet();
         }
      });
   }

}