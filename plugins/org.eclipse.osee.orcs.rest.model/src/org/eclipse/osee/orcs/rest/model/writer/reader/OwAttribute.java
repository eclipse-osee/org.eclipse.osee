/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
