/*********************************************************************
 * Copyright (c) 2025 Boeing
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

/**
 * @author Donald G. Dunne
 */
public interface BranchService {

   /**
    * Toggle branch as favorite for current user
    */
   void toggleFavoriteBranch(BranchId branch);

   /**
    * @return true if branch is marked as favorite for current users. <br/>
    * <br/>
    * Cached for efficiency as this is called thousands of times to sort favorites. Cache is updated when User artifact
    * is changed (eg: transaction in User art is different)
    */
   boolean isFavoriteBranch(BranchId branch);

}
