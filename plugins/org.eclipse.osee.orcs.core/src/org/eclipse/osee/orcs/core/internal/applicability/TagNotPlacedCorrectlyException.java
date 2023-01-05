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
package org.eclipse.osee.orcs.core.internal.applicability;

public class TagNotPlacedCorrectlyException extends Exception {

   public TagNotPlacedCorrectlyException() {
   }

   public TagNotPlacedCorrectlyException(String message) {

      super(message);
   }

}
