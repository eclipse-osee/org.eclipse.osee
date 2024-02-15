/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

public class MergeAttributeConflictData {
   private AttributeTypeToken type;
   private AttributeId attrId;
   private String[] values;

   public MergeAttributeConflictData(AttributeTypeToken type, AttributeId attrId, String[] values) {
      this.setType(type);
      this.setAttrId(attrId);
      this.setValues(values);
   }

   public AttributeTypeToken getType() {
      return type;
   }

   public void setType(AttributeTypeToken type) {
      this.type = type;
   }

   public AttributeId getAttrId() {
      return attrId;
   }

   public void setAttrId(AttributeId attrId) {
      this.attrId = attrId;
   }

   public String[] getValues() {
      return values;
   }

   public void setValues(String[] values) {
      this.values = values;
   }
}
