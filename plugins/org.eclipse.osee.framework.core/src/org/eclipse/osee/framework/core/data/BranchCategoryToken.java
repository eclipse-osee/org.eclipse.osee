/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Audrey Denk
 */
public class BranchCategoryToken extends NamedIdBase {
   public static final BranchCategoryToken SENTINEL = new BranchCategoryToken(-1L, "Sentinel");
   public BranchCategoryToken() {
      // for JAXRS
      super(Id.SENTINEL, Named.SENTINEL);
   }

   public BranchCategoryToken(Long id, String name) {
      super(id, name);
   }

   public static BranchCategoryToken valueOf(Long id, String name) {
      return new BranchCategoryToken(id, name);
   }

}
