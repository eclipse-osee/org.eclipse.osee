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

/**
 * @author Luciano Vaglienti
 */
package org.eclipse.osee.framework.core.applicability;

public class NameValuePair {
   private String Name;
   private String Value;

   public NameValuePair() {
      this("", "");
   }

   public NameValuePair(String name, String value) {
      this.setName(name);
      this.setValue(value);
   }

   public void setName(String name) {
      this.Name = name;
   }

   public void setValue(String value) {
      this.Value = value;
   }

   public String getName() {
      return this.Name;
   }

   public String getValue() {
      return this.Value;
   }
}