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

package org.eclipse.osee.define.operations.api.publisher.dataaccess;

import org.eclipse.osee.framework.core.data.ArtifactId;

/**
 * Enumeration used to categorize the results of an operation.
 */

public enum Cause {

   /**
    * The artifact identifier could not be determined to be an {@link ArtifactId} instance or a {@link String} instance
    * representing an artifact identifier or GUID.
    */

   ARTIFACT_IDENTIFIER_TYPE_ERROR,

   /**
    * The operation resulted in a duplicate entry when not expected.
    */

   DUPLICATE,

   /**
    * A uncategorized error occurred.
    */

   ERROR,

   /**
    * The operation resulted in more than one result when only one result was expected.
    */

   MORE_THAN_ONE,

   /**
    * The operation did not find a result when one was expected.
    */

   NOT_FOUND;

}
/* EOF */
