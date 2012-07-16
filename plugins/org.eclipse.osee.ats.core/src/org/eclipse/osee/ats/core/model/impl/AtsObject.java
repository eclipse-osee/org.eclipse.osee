/*
 * Created on Feb 13, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.model.impl;

import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.framework.core.data.NamedIdentity;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;

/**
 * @author Donald G. Dunne
 */
public class AtsObject extends NamedIdentity<String> implements IAtsObject {

   private String humanReadableId;
   private String desc;

   public AtsObject(String name, String guid, String hrid) {
      super(guid, name);
      this.humanReadableId = hrid;
   }

   public AtsObject(String name) {
      this(name, org.eclipse.osee.framework.jdk.core.util.GUID.create(), HumanReadableId.generate());
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

   @Override
   public String toString() {
      return getName();
   }

   public final String toStringWithId() {
      return String.format("[%s][%s]", getHumanReadableId(), getName());
   }

}
