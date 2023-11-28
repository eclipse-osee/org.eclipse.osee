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
 * Enumeration used to indicate whether or not to include deleted artifacts and attributes.
 */

public enum IncludeDeleted {

   /**
    * Do not load deleted artifacts or deleted
    */

   NO,

   /**
    * Include deleted artifacts and attributes in the load.
    */

   YES;

   /**
    * Predicate to test if the enumeration member is {@link IncludeDeleted#NO}.
    *
    * @return <code>true</code> when the member is {@link IncludeDeleted#NO}; otherwise <code>false</code>.
    */

   public boolean no() {
      return this == NO;
   }

   /**
    * Predicate to test if the enumeration member is {@link IncludeDeleted#NO}.
    *
    * @return <code>true</code> when the member is {@link IncludeDeleted#NO}; otherwise <code>false</code>.
    */

   public boolean yes() {
      return this == YES;
   }
}

/* EOF */
