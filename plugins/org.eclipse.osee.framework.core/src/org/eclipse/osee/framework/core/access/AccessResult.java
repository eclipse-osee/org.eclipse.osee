/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access;

import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public class AccessResult {

   boolean fullAccess = false;
   XResultData rd;

   public AccessResult(XResultData rd) {
      super();
      this.rd = rd;
   }

   public boolean isFullAccess() {
      return fullAccess;
   }

   public void setFullAccess(boolean fullAccess) {
      this.fullAccess = fullAccess;
   }

   public XResultData getRd() {
      return rd;
   }

   public void setRd(XResultData rd) {
      this.rd = rd;
   }

   @Override
   public String toString() {
      return String.format("Full Access: %s\n%s", fullAccess, rd.toString());
   }
}
