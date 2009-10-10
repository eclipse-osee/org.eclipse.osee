/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.skynet.core.IOseeType;

/**
 * @author Roberto E. Escobar
 */
public enum CoreBranches implements IOseeType {
   COMMON("Common", "AyH_fDpMERA+zDfML4gA"),
   SYSTEM_ROOT("System Root Branch", "AyH_fDnM2RFEhyybolQA");

   private final String name;
   private final String guid;

   private CoreBranches(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}
