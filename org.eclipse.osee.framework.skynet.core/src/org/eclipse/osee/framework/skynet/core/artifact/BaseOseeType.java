package org.eclipse.osee.framework.skynet.core.artifact;

public class BaseOseeType {
   public final static int UNPERSISTTED_VALUE = Integer.MIN_VALUE;

   private final String name;
   private final String guid;
   private int uniqueId;

   protected BaseOseeType(String guid, String name) {
      this.name = name;
      this.guid = guid;
      this.uniqueId = UNPERSISTTED_VALUE;
   }

   public final int getTypeId() {
      return uniqueId;
   }

   public final void setTypeId(int uniqueId) {
      if (this.uniqueId == -1) {
         this.uniqueId = uniqueId;
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

}
