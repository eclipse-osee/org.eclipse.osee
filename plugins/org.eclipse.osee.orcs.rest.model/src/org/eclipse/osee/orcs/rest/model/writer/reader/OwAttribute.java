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

package org.eclipse.osee.orcs.rest.model.writer.reader;

import java.util.LinkedList;
import java.util.List;

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */
public class OwAttribute {

   public OwAttributeType type;
   public List<Object> values;
   public String data;

   public OwAttributeType getType() {
      return type;
   }

   public void setType(OwAttributeType type) {
      this.type = type;
   }

   public List<Object> getValues() {
      if (values == null) {
         values = new LinkedList<>();
      }
      return values;
   }

   public void setValues(List<Object> values) {
      this.values = values;
   }

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   @Override
   public String toString() {
      return "OwAttribute [type=" + type + ", values=" + values + ", data=" + data + "]";
   }

}
