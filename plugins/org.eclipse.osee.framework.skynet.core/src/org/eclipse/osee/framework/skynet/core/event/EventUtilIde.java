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

package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;

/**
 * @author Roberto E. Escobar
 */
public final class EventUtilIde {
   private static BranchIdEventFilter commonBranchUuidEvenFilter = new BranchIdEventFilter(CoreBranches.COMMON);

   private EventUtilIde() {
      // Utility Class
   }

   public static BranchIdEventFilter getCommonBranchFilter() {
      return EventUtilIde.commonBranchUuidEvenFilter;
   }

}