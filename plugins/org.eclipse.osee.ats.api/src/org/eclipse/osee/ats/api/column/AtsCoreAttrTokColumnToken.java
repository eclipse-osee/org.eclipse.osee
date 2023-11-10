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

package org.eclipse.osee.ats.api.column;

import org.eclipse.osee.ats.api.config.AtsDisplayHint;
import org.eclipse.osee.ats.api.config.MultiEdit;
import org.eclipse.osee.ats.api.config.Show;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsCoreAttrTokColumnToken extends AtsCoreColumnToken {
   private long attrTypeId;
   private String attrTypeName;

   public AtsCoreAttrTokColumnToken() {
      // For JaxRs Instantiation
   }

   public AtsCoreAttrTokColumnToken(AttributeTypeToken attrType) {
      super(attrType.getName(), //
         attrType.getUnqualifiedName(), //
         AtsColumnUtil.getColumnWidth(attrType), //
         AtsColumnUtil.getColumnType(attrType), //
         AtsColumnUtil.getColumnAlign(attrType), //
         Show.No, //
         (attrType.hasDisplayHint(AtsDisplayHint.Edit)) ? MultiEdit.Yes : MultiEdit.No, //
         attrType.getDescription());
      this.attrTypeId = attrType.getId();
      this.attrTypeName = attrType.getName();
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
