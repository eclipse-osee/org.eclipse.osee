/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public enum WidgetOption {
   NONE,

   RFT, // REQUIRED_FOR_TRANSITION
   NOT_RFT, // NOT RFT

   LRFT, // LEAD_REQUIRED_FOR_TRANSITION
   NOT_LRFT, // NOT LRFT

   SAVE, // Automatically Save
   NOT_SAVE,

   VALIDATE_DATE,

   RFC,
   NOT_RFC,

   ENABLED,
   NOT_ENABLED,

   EDITABLE,
   NOT_EDITABLE,

   FUTURE_DATE_REQUIRED,
   NOT_FUTURE_DATE_REQUIRED,

   MULTI_SELECT,
   SINGLE_SELECT,
   //if you want read-only widget with no selection
   NO_SELECT,

   HORZ_LABEL,
   VERT_LABEL,

   LABEL_AFTER,
   LABEL_BEFORE,

   NO_LABEL,

   SORTED,
   SORTED_ASC,
   SORTED_DES,

   ADD_DEFAULT_VALUE,
   NO_DEFAULT_VALUE,

   COMPOSITE_4,
   COMPOSITE_6,
   COMPOSITE_8,
   COMPOSITE_10,
   COMPOSITE_END,

   // Fill Options
   FILL_NONE,
   FILL_HORZ,
   FILL_VERT,

   // Align Options
   ALIGN_LEFT,
   ALIGN_RIGHT,
   ALIGN_CENTER,

   // Widget Type
   WALKTHROUGH, //

   REQUIRED_FOR_FORMAL_REVIEW;

}
