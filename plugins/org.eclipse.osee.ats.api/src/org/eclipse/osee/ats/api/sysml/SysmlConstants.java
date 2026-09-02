/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.api.sysml;

/**
 * Constants for SysML V2 textual notation generation.
 *
 * @author Donald G. Dunne
 */
public final class SysmlConstants {

   public static final String BOOLEAN = "Boolean";
   public static final String INTEGER = "Integer";
   public static final String REAL = "Real";
   public static final String STRING = "String";

   public static final String MULTIPLICITY_ONE = "";
   public static final String MULTIPLICITY_ZERO_OR_ONE = "[0..1]";
   public static final String MULTIPLICITY_ZERO_OR_MORE = "[0..*]";
   public static final String MULTIPLICITY_ONE_OR_MORE = "[1..*]";

   private SysmlConstants() {
      // utility
   }
}
