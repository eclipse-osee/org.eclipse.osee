/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.model.internal.fields.UniqueIdField;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractOseeType extends NamedIdBase implements IOseeStorable {

   public static final String NAME_FIELD_KEY = "osee.name.field";
   public static final String UNIQUE_ID_FIELD_KEY = "osee.unique.id.field";

   private StorageState itemState = StorageState.CREATED;
   private final Map<String, IOseeField<?>> fieldMap = new HashMap<>();

   protected AbstractOseeType(Long id, String name) {
      super(id, name);
      addField(UNIQUE_ID_FIELD_KEY, new UniqueIdField());
      addField(NAME_FIELD_KEY, new OseeField<>(name));
   }

   protected synchronized void addField(String key, AbstractOseeField<?> toAdd) {
      fieldMap.put(key, toAdd);
   }

   public Set<String> getFieldNames() {
      return fieldMap.keySet();
   }

   @SuppressWarnings("unchecked")
   protected <T> IOseeField<T> getField(String key) {
      IOseeField<T> field = (AbstractOseeField<T>) fieldMap.get(key);
      Conditions.checkNotNull(field, key);
      return field;
   }

   public boolean isFieldDirty(String key) {
      return getField(key).isDirty();
   }

   public boolean areFieldsDirty(String... keys) {
      boolean result = false;
      for (String key : keys) {
         result |= isFieldDirty(key);
      }
      return result;
   }

   protected <T> T getFieldValue(String key) {
      IOseeField<T> field = getField(key);
      return field.get();
   }

   protected <T> T getFieldValueLogException(T defaultValue, String key) {
      T value = defaultValue;
      try {
         value = getFieldValue(key);
      } catch (OseeCoreException ex) {
         OseeLog.log(AbstractOseeType.class, Level.SEVERE, ex);
      }
      return value;
   }

   protected <T> void setField(String key, T value) {
      IOseeField<T> field = getField(key);
      field.set(value);
      if (field.isDirty()) {
         StorageState oldState = getStorageState();
         if (StorageState.CREATED != oldState && StorageState.PURGED != oldState) {
            setStorageState(StorageState.MODIFIED);
         }
      }
   }

   protected <T> void setFieldLogException(String key, T value) {
      try {
         setField(key, value);
      } catch (OseeCoreException ex) {
         OseeLog.log(AbstractOseeType.class, Level.SEVERE, ex);
      }
   }

   @Override
   public String getName() {
      return getFieldValueLogException("", NAME_FIELD_KEY);
   }

   @Override
   public void setName(String name) {
      setFieldLogException(NAME_FIELD_KEY, name);
   }

   @Override
   public final boolean isDirty() {
      boolean isDirty = false;
      for (IOseeField<?> field : fieldMap.values()) {
         if (field.isDirty()) {
            isDirty = true;
            break;
         }
      }
      return isDirty;
   }

   @Override
   public final void clearDirty() {
      if (StorageState.PURGED != getStorageState()) {
         setStorageState(StorageState.LOADED);
      }
      for (IOseeField<?> field : fieldMap.values()) {
         field.clearDirty();
      }
   }

   @Override
   public StorageState getStorageState() {
      return itemState;
   }

   public void setStorageState(StorageState storageState) {
      this.itemState = storageState;
   }
}
