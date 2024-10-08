/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.disposition.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author Angel Avila
 */
public enum DispoSetStatus {
   ALL(0, "All"),
   NONE(1, "None"),
   NO_CHANGE(2, "No Change"),
   OK(3, "OK"),
   WARNINGS(4, "Warnings"),
   FAILED(5, "Failed");

   private Integer value;
   private String name;

   @JsonCreator
   public static DispoSetStatus forValue(String value) {
      for (DispoSetStatus status : DispoSetStatus.values()) {
         if (value.equals(status.getName())) {
            return status;
         }
      }
      return null;
   }

   @JsonValue
   public String toValue() {
      for (DispoSetStatus status : DispoSetStatus.values()) {
         if (status.equals(this)) {
            return status.getName();
         }
      }
      return null;
   }

   DispoSetStatus() {

   }

   DispoSetStatus(int value, String name) {
      this.value = value;
      this.name = name;
   }

   public int getValue() {
      return value;
   }

   public String getName() {
      return name;
   }

   public void setValue(Integer value) {
      this.value = value;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isFailed() {
      return value.equals(FAILED.value);
   }
}
