/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.orcs.data;

public class OrcsPurgeResult {
   private String message;
   private boolean error;

   public OrcsPurgeResult(String message, boolean error) {
      setMessage(message);
      setError(error);
   }

   public String getMessage() {
      return message;
   }

   public void setMessage(String message) {
      this.message = message;
   }

   public boolean isError() {
      return error;
   }

   public void setError(boolean error) {
      this.error = error;
   }
}
