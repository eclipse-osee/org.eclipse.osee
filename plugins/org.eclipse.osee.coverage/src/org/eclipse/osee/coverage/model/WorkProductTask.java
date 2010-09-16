/*
 * Created on Sep 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.coverage.model;

public class WorkProductTask {

   String guid;
   String name;
   boolean completed;
   WorkProductAction parent;

   public WorkProductTask(String guid, String name, boolean completed, WorkProductAction parent) {
      super();
      this.guid = guid;
      this.name = name;
      this.completed = completed;
      this.parent = parent;
   }

   public String getGuid() {
      return guid;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isCompleted() {
      return completed;
   }

   public void setCompleted(boolean completed) {
      this.completed = completed;
   }

   public WorkProductAction getParent() {
      return parent;
   }

   public void setParent(WorkProductAction parent) {
      this.parent = parent;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((guid == null) ? 0 : guid.hashCode());
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
      WorkProductTask other = (WorkProductTask) obj;
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
