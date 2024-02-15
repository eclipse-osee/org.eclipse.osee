/*********************************************************************
 * Copyright (c) 2023 Boeing
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

package org.eclipse.osee.framework.core.publishing;

/**
 * Enumeration to specify if a function should filter results for view applicability.
 *
 * @author Loren K. Ashley
 */

public enum FilterForView {

   /**
    * Do not filter for view.
    */

   NO,

   /**
    * Filter for view.
    */

   YES;

   /**
    * Predicate to test if the enumeration member is {@link FilterForView#NO}.
    *
    * @return <code>true</code> when the member is {@link FilterForView#NO}; otherwise <code>false</code>.
    */

   boolean no() {
      return this == NO;
   }

   /**
    * Predicate to test if the enumeration member is {@link FilterForView#YES}.
    *
    * @return <code>true</code> when the member is {@link FilterForView#YES}; otherwise <code>false</code>.
    */

   boolean yes() {
      return this == YES;
   }

}

/* EOF */