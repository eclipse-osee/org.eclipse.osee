package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.IOseeType;

public class BaseOseeType implements IOseeType {
   public final static int UNPERSISTTED_VALUE = Integer.MIN_VALUE;

   private String name;
   private final String guid;
   private int uniqueId;
   private boolean dirty;
   private ModificationType modificationType;

   protected BaseOseeType(String guid, String name) {
      this.name = name;
      this.guid = guid;
      this.uniqueId = UNPERSISTTED_VALUE;
      this.dirty = true;
      this.modificationType = ModificationType.NEW;
   }

   public final int getTypeId() {
      return uniqueId;
   }

   public final void setTypeId(int uniqueId) throws OseeStateException {
      if (this.uniqueId == UNPERSISTTED_VALUE) {
         updateDirty(this.uniqueId, uniqueId);
         this.uniqueId = uniqueId;
      } else {
         throw new OseeStateException("can not change the type id once it has been set");
      }
   }

   public final String getGuid() {
      return guid;
   }

   public final String getName() {
      return name;
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
      if (obj instanceof IOseeType) {
         return guid.equals(((IOseeType) obj).getGuid());
      }

      if (getClass() != obj.getClass()) {
         return false;
      }
      return guid.equals(((BaseOseeType) obj).guid);
   }

   /**
    * Sets the type name in memory, but does not persist to the data store
    */
   public void setName(String name) {
      updateDirty(this.name, name);
      this.name = name;
   }

   /**
    * @return the dirty
    */
   public boolean isDirty() {
      return dirty;
   }

   public ModificationType getModificationType() {
      return modificationType;
   }

   public void setModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }

   public void clearDirty() {
      dirty = false;
   }

   protected void updateDirty(Object original, Object other) {
      if (isDifferent(original, other)) {
         dirty = true;
      }
   }

   @SuppressWarnings("unchecked")
   protected boolean isDifferent(Object original, Object other) {
      boolean result = true;
      if (original == null && other == null) {
         result = false;
      } else if (original != null && other != null) {
         if (original instanceof Collection<?> && other instanceof Collection<?>) {
            result = isDifferent((Collection<Object>) original, (Collection<Object>) other);
         } else {
            result = !original.equals(other);
         }
      }
      return result;
   }

   private boolean isDifferent(Collection<Object> original, Collection<Object> other) {
      return !Collections.setComplement(original, other).isEmpty() || //
      !Collections.setComplement(other, original).isEmpty();
   }
}