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

package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public class JaxAtsWorkDef {

   private String name;
   private String workDefDsl;

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getWorkDefDsl() {
      return workDefDsl;
   }

   public void setWorkDefDsl(String workDefDsl) {
      this.workDefDsl = workDefDsl;
   }

}
