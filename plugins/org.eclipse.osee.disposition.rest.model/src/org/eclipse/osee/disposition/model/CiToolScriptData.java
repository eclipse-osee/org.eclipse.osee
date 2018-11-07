/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
