/*********************************************************************
 * Copyright (c) 2024 Boeing
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
 * Enumeration used to indicate if an artifact should be recursively processed for publishing.
 *
 * @author Loren K. Ashley
 */

public enum ProcessRecursively {

   /**
    * Do not publish the artifact's children.
    */

   NO,

   /**
    * Recursively publish the artifact's children.
    */

   YES;

   /**
    * Predicate to determine if an artifact's children should not be published.
    *
    * @return <code>true</code> when the enumeration member is {@link ProcessRecursively#NO}; otherwise,
    * <code>false</code>.
    */

   public boolean no() {
      return this == NO;
   }

   /**
    * Predicate to determine if an artifact's children should not be published.
    *
    * @return <code>true</code> when the enumeration member is {@link ProcessRecursively#NO}; otherwise,
    * <code>false</code>.
    */

   public boolean yes() {
      return this == YES;
   }

}

/* EOF */
