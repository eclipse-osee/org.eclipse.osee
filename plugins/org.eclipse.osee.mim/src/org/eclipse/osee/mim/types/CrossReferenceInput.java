/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.mim.types;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.applicability.NameValuePair;

/**
 * @author Audrey Denk
 */
public class CrossReferenceInput {
   private String name;
   private String value;
   private List<NameValuePair> values = new ArrayList<>();

   public CrossReferenceInput() {
      //Empty constructor is required  for de-serializing
      //object that need to be created by reflection
   }

   public CrossReferenceInput(String name, String value, List<NameValuePair> values) {
      this.setName(name);
      this.setValue(value);
      this.setValues(values);
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public List<NameValuePair> getValues() {
      return values;
   }

   public void setValues(List<NameValuePair> values) {
      this.values = values;
   }
}
