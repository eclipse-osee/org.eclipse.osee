/*********************************************************************
 * Copyright (c) 2017 Boeing
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

/**
 * @author Dominic A. Guss
 */
public class CiToolScriptData {

   private String script;
   private String status;

   public CiToolScriptData() {
      // Do nothing
   }

   public CiToolScriptData(String script, String status) {
      this.script = script;
      this.status = status;
   }

   public String getScript() {
      return script;
   }

   public String getStatus() {
      return status;
   }
}
