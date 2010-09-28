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
package org.eclipse.osee.ats.access;

import org.eclipse.osee.framework.core.data.AccessContextId;

/**
 * @author Donald G. Dunne
 */
public final class AtsBranchObjectContextId {

   public static final AccessContextId DEFAULT_BRANCH_CONTEXT = AtsAccessContextIdFactory.createContextId(
      "AFRkIhi2m2cdanu3i2AA", "ats.branchobject.default.context");
   public static final AccessContextId DENY_CONTEXT = AtsAccessContextIdFactory.createContextId("ABcgU0QxFG_cQU4Ph1wA",
      "ats.branchobject.deny");

   private AtsBranchObjectContextId() {
      // Branch Object Contexts;
   }
}
