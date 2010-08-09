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

/**
 * @author Donald G. Dunne
 */
public class AtsBranchObjectContextId extends AtsAccessContextId {

   public static final AtsBranchObjectContextId DEFAULT_BRANCH_CONTEXT = new AtsBranchObjectContextId(
      "AFRkIhi2m2cdanu3i2AA", "ats.branchobject.default.context");
   public static final AtsBranchObjectContextId DENY_CONTEXT = new AtsBranchObjectContextId("ABcgU0QxFG_cQU4Ph1wA",
      "ats.branchobject.deny");

   protected AtsBranchObjectContextId(String guid, String name) {
      super(guid, name);
   }

   public static AtsBranchObjectContextId get(String guid) {
      if (!guidToIds.containsKey(guid)) {
         guidToIds.put(guid, new AtsBranchObjectContextId(guid, "name unknown"));
      }
      return (AtsBranchObjectContextId) guidToIds.get(guid);
   }

}
