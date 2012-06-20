/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model.impl;

import org.eclipse.osee.ats.core.model.IAtsObject;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Donald G. Dunne
 */
public class AtsObject implements IAtsObject {

   private String humanReadableId;
   private String name;
   private String guid;

   public void setGuid(String guid) {
      this.guid = guid;
   }

   private String desc;

   public AtsObject(String name, String guid, String hrid) {
      this.name = name;
      this.guid = guid;
      this.humanReadableId = hrid;
   }

   public AtsObject(String name) {
      this(name, org.eclipse.osee.framework.jdk.core.util.GUID.create(), HumanReadableId.generate());
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public String getDescription() {
      return desc;
   }

   @Override
   public String getHumanReadableId() {
      return humanReadableId;
   }

   public void setDescription(String desc) {
      this.desc = desc;
   }

   public void setHumanReadableId(String hrid) {
      this.humanReadableId = hrid;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return getName();
   }

   public final String toStringWithId() {
      return String.format("[%s][%s]", getHumanReadableId(), getName());
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
      AtsObject other = (AtsObject) obj;
      if (guid == null) {
         if (other.guid != null) {
            return false;
         } else {
            return false;
         }
      } else if (!guid.equals(other.guid)) {
         return false;
      }
      return true;
   }

}
