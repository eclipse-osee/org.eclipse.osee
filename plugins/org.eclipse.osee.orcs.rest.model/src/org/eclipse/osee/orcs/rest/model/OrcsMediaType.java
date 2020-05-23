/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.orcs.rest.model;

import javax.ws.rs.core.MediaType;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsMediaType {

   private OrcsMediaType() {
      // Constants
   }

   /**
    * A {@code String} constant representing "{@value #APPLICATION_ORCS_TYPES}" media type.
    */
   public final static String APPLICATION_ORCS_TYPES = "application/orcs-types+osee";

   /**
    * A {@link MediaType} constant representing "{@value #APPLICATION_ORCS_TYPES}" media type.
    */
   public final static MediaType APPLICATION_ORCS_TYPES_TYPE = new MediaType("application", "orcs-types+osee");

}