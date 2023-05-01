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

package org.eclipse.osee.define.api.publishing.templatemanager;

/**
 * Enumeration of the primary map keys used by the {@link AbstractPublishingTemplateCache} for indexing publishing
 * templates.
 *
 * @author Loren K. Ashley
 */

public enum PublishingTemplateKeyType {

   /**
    * Publishing templates are hashed by identifiers under this primary key.
    */

   IDENTIFIER,

   /**
    * Publishing templates are hashed by their template match criteria under this primary key.
    */

   MATCH_CRITERIA,

   /**
    * Publishing templates are hashed by name under this primary key.
    */

   NAME,

   /**
    * Publishing templates are hashed by safe name under this primary key.
    */

   SAFE_NAME;

}

/* EOF */
