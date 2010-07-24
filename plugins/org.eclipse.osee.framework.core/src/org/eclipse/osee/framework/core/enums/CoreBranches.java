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
package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Roberto E. Escobar
 */
public class CoreBranches extends NamedIdentity implements IOseeBranch {
   public static final CoreBranches COMMON = new CoreBranches("AyH_fDpMERA+zDfML4gA", "Common");
   public static final CoreBranches SYSTEM_ROOT = new CoreBranches("AyH_fDnM2RFEhyybolQA", "System Root Branch");

   public CoreBranches(String guid, String name) {
      super(guid, name);
   }
}