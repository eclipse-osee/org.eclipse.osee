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
 * Enumeration used to indicate if the publisher should restart document numbering.
 *
 * @author Loren K. Ashley
 */

public enum RestartNumbering {

   /**
    * Do not restart numbering.
    */

   NO,

   /**
    * Restart numbering.
    */

   YES;

   /**
    * Predicate to determine if numbering should not be restarted.
    *
    * @return <code>true</code> when the enumeration member is {@link RestartNumbering#NO}; otherwise,
    * <code>false</code>.
    */

   public boolean no() {
      return this == NO;
   }

   /**
    * Predicate to determine if numbering should be restarted.
    *
    * @return <code>true</code> when the enumeration member is {@link RestartNumbering#YES}; otherwise,
    * <code>false</code>.
    */

   public boolean yes() {
      return this == YES;
   }
}

/* EOF */