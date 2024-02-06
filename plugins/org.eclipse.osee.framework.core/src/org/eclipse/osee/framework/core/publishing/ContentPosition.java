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
 * An enumeration to indicate the position of the main publishing content with respect to the published attributes.
 *
 * @author Loren K. Ashley
 */

public enum ContentPosition {

   /**
    * Indicates the main publishing content comes at the start of the publishing content for the artifact.
    */

   START,

   /**
    * Indicates the main publishing content comes at the end of the publishing content for the artifact.
    */

   END;

   /**
    * Predicate to determine if the {@link ContentPosition} is {@link ContentPosition#Start}.
    *
    * @return <code>true</code> when the {@link ContentPosition} is {@link ContentPosition#Start}; otherwise,
    * <code>false</code>.
    */

   public boolean isStart() {
      return this == START;
   }

   /**
    * Predicate to determine if the {@link ContentPosition} is {@link ContentPosition#End}.
    *
    * @return <code>true</code> when the {@link ContentPosition} is {@link ContentPosition#End}; otherwise,
    * <code>false</code>.
    */

   public boolean isEnd() {
      return this == END;
   }

}

/* EOF */
