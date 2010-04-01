/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.event.res.event;

import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.util.AXml;

/**
 * @author Donald G. Dunne
 */
public class AttributeChange {
   private String attrTypeGuid;
   private String modificationGuid;
   private int attributeId;
   private int gammaId;
   private Object[] value;

   public AttributeChange(String typeGuid, Object[] value, ModificationType modificationType, int attributeId, int gammaId) {
      super();
      this.attrTypeGuid = typeGuid;
      this.value = value;
      this.modificationGuid = AttributeEventModificationType.getType(modificationType).getGuid();
      this.attributeId = attributeId;
      this.gammaId = gammaId;
   }

   public AttributeChange(String xml) {
      fromXml(xml);
   }

   public String toXml() {
      StringBuffer sb = new StringBuffer();
      sb.append(AXml.addTagData("aTypeGuid", "" + attrTypeGuid));
      sb.append(AXml.addTagData("modGuid", "" + modificationGuid));
      sb.append(AXml.addTagData("attrId", "" + attributeId));
      sb.append(AXml.addTagData("gammaId", "" + gammaId));
      sb.append(AXml.addTagData("value", "" + value));
      return sb.toString();
   }

   private void fromXml(String xml) {
      this.attrTypeGuid = AXml.getTagData(xml, "aTypeGuid");
      this.modificationGuid = AXml.getTagData(xml, "modGuid");
      this.attributeId = AXml.getTagIntegerData(xml, "attrId");
      this.gammaId = AXml.getTagIntegerData(xml, "gammaId");
      System.err.println("AttributeChange: how transmit value");
      //      this.value = AXml.getTagData(xml, "value");
   }

   @Override
   public String toString() {
      return attrTypeGuid + "(" + attributeId + ")" + " => " + value;
   }

   public String getAttributeTypeGuid() {
      return attrTypeGuid;
   }

   public Object[] getData() {
      return value;
   }

   public int getAttributeId() {
      return attributeId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public ModificationType getModificationType() {
      return AttributeEventModificationType.getType(modificationGuid).getModificationType();
   }

}
