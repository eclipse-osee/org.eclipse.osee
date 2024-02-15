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

import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * Enumeration used to indicate whether metadata attributes are to be included in a publish.
 *
 * @author Loren K. Ashley
 */

public enum IncludeMetadataAttributes {

   /**
    * Metadata attributes are to be processed for the publish.
    */

   ALWAYS,

   /**
    * Metadata attributes are not to be included in the publish.
    */

   NEVER,

   /**
    * Metadata attributes are only to be included when the artifact has main content.
    */

   ONLY_WITH_MAIN_CONTENT,

   /**
    * Metadata attributes are only to be included for artifacts of the types or derived from the types
    * {@link CoreArtifactTypes#Requirement} or {@link CoreArtifactTypes#DesignMsWord}.
    *
    * @implNote There is not a format agnostic design artifact type.
    */

   ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD;

   /**
    * Predicate to determine if the member is {@link #ALWAYS}.
    *
    * @return <code>true</code> when the member is {@link #ALWAYS}; otherwise <code>false</code>.
    */

   public boolean isAlways() {
      return this == ALWAYS;
   }

   /**
    * Predicate to determine if the member is {@link #NEVER}.
    *
    * @return <code>true</code> when the member is {@link #NEVER}; otherwise <code>false</code>.
    */

   public boolean isNever() {
      return this == NEVER;
   }

   /**
    * Predicate to determine if the member is {@link #ONLY_WITH_MAIN_CONTENT}.
    *
    * @return <code>true</code> when the member is {@link #ONLY_WITH_MAIN_CONTENT}; otherwise <code>false</code>.
    */

   public boolean isOnlyWithMainContent() {
      return this == ONLY_WITH_MAIN_CONTENT;
   }

   /**
    * Predicate to determine if the member is {@link #ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD}.
    *
    * @return <code>true</code> when the member is {@link #ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD}; otherwise
    * <code>false</code>.
    */

   public boolean isOnlyWithRequirementOrDesignMsWord() {
      return this == ONLY_WITH_REQUIREMENT_OR_DESIGN_MSWORD;
   }

}

/* EOF */
