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
package org.eclipse.osee.mim.types;

public class ApplyResult  {
   private boolean success;
   private String statusText;
   
   public ApplyResult() {
      //
   }
   public ApplyResult(boolean success, String statusText) {
      this.setSuccess(success);
      this.setStatusText(statusText);
   }
   public boolean isSuccess() {
      return success;
   }
   public void setSuccess(boolean success) {
      this.success = success;
   }
   public String getStatusText() {
      return statusText;
   }
   public void setStatusText(String statusText) {
      this.statusText = statusText;
   }
   
   
}
