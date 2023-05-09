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

package org.eclipse.osee.framework.core.publishing;

/**
 * CUI Types are either CUI Basic or CUI Specified. CUI Basic information is handled according to the basic CUI rules.
 * CUI Specified information has specific handling and marking requirements specified by the controlling authority.
 *
 * @author Loren K. Ashley
 */

public enum CuiTypeIndicator {

   /**
    * Indicator for CUI Basic. CUI Basic is handled according to the uniform set of controls set forth in the CFR and
    * the CUI Registry.
    */

   BASIC,

   /**
    * Indicator for CUI Specified. CUI Specified is different in that the authorizing law, regulation, or
    * Government-wide policy contains specific handling controls that differ from those for CUI Basic.
    */

   SPECIFIED;

}

/* EOF */
