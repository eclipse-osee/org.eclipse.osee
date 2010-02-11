package org.eclipse.osee.framework.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.core.internal.fields.AbstractOseeField;
import org.eclipse.osee.framework.core.internal.fields.IOseeField;
import org.eclipse.osee.framework.core.internal.fields.OseeField;
import org.eclipse.osee.framework.core.internal.fields.UniqueIdField;
import org.eclipse.osee.framework.logging.OseeLog;

public abstract class AbstractOseeType extends NamedIdentity implements IOseeStorable {

   public static final String NAME_FIELD_KEY = "osee.name.field";
   public static final String UNIQUE_ID_FIELD_KEY = "osee.unique.id.field";

   private StorageState itemState;
   private final Map<String, IOseeField<?>> fieldMap;

   protected AbstractOseeType(String guid, String name) {
      super(guid, name);
      this.fieldMap = new HashMap<String, IOseeField<?>>();
      this.itemState = StorageState.CREATED;

      addField(UNIQUE_ID_FIELD_KEY, new UniqueIdField());
      addField(NAME_FIELD_KEY, new OseeField<String>(name));
   }

   protected synchronized void addField(String key, AbstractOseeField<?> toAdd) {
      fieldMap.put(key, toAdd);
   }

   public Set<String> getFieldNames() {
      return fieldMap.keySet();
   }

   @SuppressWarnings("unchecked")
   protected <T> IOseeField<T> getField(String key) throws OseeCoreException {
      IOseeField<T> field = (AbstractOseeField<T>) fieldMap.get(key);
      if (field == null) {
         throw new OseeArgumentException(String.format("Field [%s] was null", key));
      }
      return field;
   }

   public boolean isFieldDirty(String key) throws OseeCoreException {
      return getField(key).isDirty();
   }

   public boolean areFieldsDirty(String... keys) throws OseeCoreException {
      boolean result = false;
      for (String key : keys) {
         result |= isFieldDirty(key);
      }
      return result;
   }

   protected <T> T getFieldValue(String key) throws OseeCoreException {
      IOseeField<T> field = getField(key);
      return field.get();
   }

   protected <T> T getFieldValueLogException(T defaultValue, String key) {
      T value = defaultValue;
      try {
         value = getFieldValue(key);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return value;
   }

   protected <T> void setField(String key, T value) throws OseeCoreException {
      IOseeField<T> field = getField(key);
      field.set(value);
   }

   protected <T> void setFieldLogException(String key, T value) {
      try {
         setField(key, value);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public final int getId() {
      return getFieldValueLogException(IOseeStorable.UNPERSISTED_VALUE, UNIQUE_ID_FIELD_KEY);
   }

   @Override
   public final String getName() {
      return getFieldValueLogException("", NAME_FIELD_KEY);
   }

   public final void setId(int uniqueId) throws OseeCoreException {
      setField(UNIQUE_ID_FIELD_KEY, uniqueId);
   }

   public void setName(String name) {
      setFieldLogException(NAME_FIELD_KEY, name);
   }

   @Override
   public String toString() {
      return String.format("%s - [%s]", getName(), getGuid());
   }

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

   public final void clearDirty() {
      for (IOseeField<?> field : fieldMap.values()) {
         field.clearDirty();
      }
   }

   public StorageState getStorageState() {
      return itemState;
   }

   public void setStorageState(StorageState storageState) {
      this.itemState = storageState;
   }
}