/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.api.task;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Donald G. Dunne
 */
public class JaxAttribute {

   private AttributeTypeToken attrType;
   private List<Object> values;

   public List<Object> getValues() {
      if (values == null) {
         values = new LinkedList<>();
      }
      return values;
   }

   public void setValues(List<Object> values) {
      this.values = values;
   }

   @Override
   public String toString() {
      return "JaxAttribute [attrTypeName=" + attrType + ", values=" + values + "]";
   }

   public AttributeTypeToken getAttrType() {
      return attrType;
   }

   public void setAttrType(AttributeTypeToken attrType) {
      this.attrType = attrType;
   }

}
