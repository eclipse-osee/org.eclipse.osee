/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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