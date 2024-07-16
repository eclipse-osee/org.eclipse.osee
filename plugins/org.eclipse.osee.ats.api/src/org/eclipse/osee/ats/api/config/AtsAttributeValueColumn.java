/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.ats.api.column.AtsValueColumn;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeValueColumn extends AtsValueColumn {
   private long attrTypeId;
   private String attrTypeName;

   public AtsAttributeValueColumn() {
      // For JaxRs Instantiation
   }

   public AtsAttributeValueColumn(AttributeTypeToken attributeType, String id, String name, int width, ColumnType columnType, ColumnAlign align, Show show, //
      MultiEdit multi, String description) {
      this(attributeType, id, name, width, columnType, align, show, multi, ActionRollup.No, InheritParent.No,
         description);
   }

   public AtsAttributeValueColumn(AttributeTypeToken attributeType, String id, String name, int width, ColumnType columnType, ColumnAlign align, Show show, //
      MultiEdit multi, ActionRollup actionRollup, InheritParent inheritParent, String description) {
      this(attributeType, id, name, width, align.name(), show.yes(), columnType, multi.yes(), description,
         actionRollup.yes(), inheritParent.yes());
   }

   /**
    * Use non-boolean constructors above
    */
   @Deprecated
   public AtsAttributeValueColumn(AttributeTypeToken attributeType, String id, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description, Boolean actionRollup, Boolean inheritParent) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description, actionRollup, inheritParent);
      this.attrTypeId = attributeType.getId();
      this.attrTypeName = attributeType.getName();
   }

   public long getAttrTypeId() {
      return attrTypeId;
   }

   public void setAttrTypeId(long attrTypeId) {
      this.attrTypeId = attrTypeId;
   }

   public String getAttrTypeName() {
      return attrTypeName;
   }

   public void setAttrTypeName(String attrTypeName) {
      this.attrTypeName = attrTypeName;
   }

   @Override
   public String toString() {
      return "AtsAttributeValueColumn [name=" + getName() + ", namespace=" + getNamespace() + ", attrTypeId=" + attrTypeId + ", attrTypeName=" + attrTypeName + "]";
   }

   @Override
   public String getId() {
      String result = null;
      if (Strings.isValid(super.getId())) {
         result = super.getId();
      } else if (Strings.isValid(attrTypeName)) {
         result = attrTypeName;
      }
      return result;
   }

}
