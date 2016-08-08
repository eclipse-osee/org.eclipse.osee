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
import org.eclipse.osee.framework.core.data.TokenFactory;

/**
 * @author Roberto E. Escobar
 */
public final class CoreBranches {

   public static final IOseeBranch COMMON = TokenFactory.createBranch(570, "Common");
   public static final IOseeBranch SYSTEM_ROOT = TokenFactory.createBranch(1, "System Root Branch");
   public static final Long SYSTEM_ROOT_ID = SYSTEM_ROOT.getUuid();

   public CoreBranches() {
      // Constants
   }
}