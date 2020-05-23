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

/**
 * Data Transfer object for Orcs Writer
 *
 * @author Donald G. Dunne
 */
public class OwApplicability {

   public Long appId;
   public String value;
   public String data;

   public String getData() {
      return data;
   }

   public void setData(String data) {
      this.data = data;
   }

   @Override
   public String toString() {
      return "OwApplicability [appId=" + getAppId() + ", data=" + data + "]";
   }

   public Long getAppId() {
      return appId;
   }

   public void setAppId(Long appId) {
      this.appId = appId;
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
   }

}
