/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.core.enums;

import org.eclipse.osee.framework.core.data.IOseeBranch;

/**
 * @author Roberto E. Escobar
 */
public final class CoreBranches {

   public static final IOseeBranch COMMON = IOseeBranch.create(570, "Common");
   public static final IOseeBranch SYSTEM_ROOT = IOseeBranch.create(1, "System Root Branch");

   private CoreBranches() {
      // Constants
   }
}