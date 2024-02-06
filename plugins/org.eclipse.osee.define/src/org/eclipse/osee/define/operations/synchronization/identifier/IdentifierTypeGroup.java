/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.operations.synchronization.identifier;

import org.eclipse.osee.define.operations.synchronization.LinkType;

/**
 * Synchronization Artifacts things are grouped into categories for things with a similar function and structure.
 *
 * @author Loren K. Ashley
 */

public enum IdentifierTypeGroup implements LinkType {

   /**
    * Things that define an attribute of provide an attribute value.
    */

   ATTRIBUTE,

   /**
    * Things that contain a "unit" of data are categorized as objects. This group includes the Synchronization Artifact
    * Specification, Spec Object, Specter Spec Object, and Spec Relations.
    */

   OBJECT,

   /**
    * Things that can be the source or target of a relationship. This group includes the Synchronization Artifact Spec
    * Object and Specter Spec Objects.
    */

   RELATABLE_OBJECT,

   /**
    * Non-root level things that contain a "unit" of data are categorized as subordinate objects. This group includes
    * the Synchronization Artifact Spec Object, Specter Spec Object, and Spec Relations.
    */

   SUBORDINATE_OBJECT,

   /**
    * Things that are used in the Synchronization Artifact DOM are members of this group.
    */

   SYNCHRONIZATION_ARTIFACT_DOM,

   /**
    * Things that define the structure of an {@link OBJECT} are categorized as types. This group includes the
    * Synchronization Artifact Specification Type, Spec Object Type, and Spec Relation Types.
    */

   TYPE;
}

/* EOF */