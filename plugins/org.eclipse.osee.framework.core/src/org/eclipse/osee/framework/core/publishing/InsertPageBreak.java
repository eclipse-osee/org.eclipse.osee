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
 * Enumeration used to indicate if the publisher should insert a page break.
 *
 * @author Loren K. Ashley
 */

public enum InsertPageBreak {

   /**
    * Do not insert a page break.
    */

   NO,

   /**
    * Insert a page break.
    */

   YES;

   /**
    * Predicate to determine if a page break should not be inserted.
    *
    * @return <code>true</code> when the enumeration member is {@link InsertPageBreak#NO}; otherwise,
    * <code>false</code>.
    */

   public boolean no() {
      return this == NO;
   }

   /**
    * Predicate to determine if a page break should be inserted.
    *
    * @return <code>true</code> when the enumeration member is {@link InsertPageBreak#YES}; otherwise,
    * <code>false</code>.
    */

   public boolean yes() {
      return this == YES;
   }

}

/* EOF */