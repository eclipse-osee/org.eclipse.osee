package org.eclipse.osee.framework.core.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.internal.Activator;
import org.eclipse.osee.framework.core.internal.fields.AbstractOseeField;
import org.eclipse.osee.framework.core.internal.fields.IOseeField;
import org.eclipse.osee.framework.core.internal.fields.OseeField;
import org.eclipse.osee.framework.core.internal.fields.UniqueIdField;
import org.eclipse.osee.framework.logging.OseeLog;

public abstract class AbstractOseeType implements IOseeStorable, NamedIdentity {

   public static final String NAME_FIELD_KEY = "osee.name.field";
   public static final String UNIQUE_ID_FIELD_KEY = "osee.unique.id.field";

   private final String guid;
   private ModificationType modificationType;
   private final Map<String, IOseeField<?>> fieldMap;

   protected AbstractOseeType(String guid, String name) {
      this.guid = guid;
      this.fieldMap = new HashMap<String, IOseeField<?>>();
      this.modificationType = ModificationType.NEW;

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
   private <T> IOseeField<T> getField(String key) throws OseeCoreException {
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

   public final String getGuid() {
      return guid;
   }

   public final int getId() {
      return getFieldValueLogException(IOseeStorable.UNPERSISTTED_VALUE, UNIQUE_ID_FIELD_KEY);
   }

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

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (guid == null ? 0 : guid.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null || guid == null) {
         return false;
      }
      if (obj instanceof Identity) {
         return guid.equals(((Identity) obj).getGuid());
      }

      if (getClass() != obj.getClass()) {
         return false;
      }
      return guid.equals(((AbstractOseeType) obj).guid);
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

   public ModificationType getModificationType() {
      return modificationType;
   }

   public void setModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }
}