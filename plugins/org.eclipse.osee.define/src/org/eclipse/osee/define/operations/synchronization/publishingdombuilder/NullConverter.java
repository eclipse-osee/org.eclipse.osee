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

package org.eclipse.osee.define.operations.synchronization.publishingdombuilder;

import org.eclipse.osee.define.operations.synchronization.forest.GroveThing;

/**
 * An {@link GroveThing} converter that doesn't do anything.
 *
 * @author Loren K. Ashley
 */

public class NullConverter {

   /**
    * Private constructor to prevent instantiation of the class.
    */

   private NullConverter() {
   }

   /**
    * Converter that doesn't do anything
    *
    * @param groveThing the {@link GroveThing} thing to be converted.
    */

   static void convert(GroveThing groveThing) {
      //converter doesn't do anything
   }

}

/* EOF */
