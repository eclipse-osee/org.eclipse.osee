package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.OseeStateException;

public class BaseOseeType {
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
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      BaseOseeType other = (BaseOseeType) obj;
      if (guid == null) {
         if (other.guid != null) {
            return false;
         }
      } else if (!guid.equals(other.guid)) {
         return false;
      }
      return true;
   }

   /**
    * Sets the type name in memory, but does not persist to the data store
    */
   public void setName(String name) {
      dirty = true;
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

   public void persist() {
      dirty = false;
   }
}