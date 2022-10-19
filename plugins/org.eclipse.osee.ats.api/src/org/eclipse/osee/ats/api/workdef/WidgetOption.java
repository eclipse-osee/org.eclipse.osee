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

   REQUIRED_FOR_TRANSITION,
   NOT_REQUIRED_FOR_TRANSITION,

   AUTO_SAVE,
   NOT_AUTO_SAVE,

   VALIDATE_DATE,

   REQUIRED_FOR_COMPLETION,
   NOT_REQUIRED_FOR_COMPLETION,

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

   HORIZONTAL_LABEL,
   VERTICAL_LABEL,

   LABEL_AFTER,
   LABEL_BEFORE,

   NO_LABEL,

   SORTED,

   ADD_DEFAULT_VALUE,
   NO_DEFAULT_VALUE,

   BEGIN_COMPOSITE_4,
   BEGIN_COMPOSITE_6,
   BEGIN_COMPOSITE_8,
   BEGIN_COMPOSITE_10,
   END_COMPOSITE,

   // Fill Options
   FILL_NONE,
   FILL_HORIZONTALLY,
   FILL_VERTICALLY,

   // Align Options
   ALIGN_LEFT,
   ALIGN_RIGHT,
   ALIGN_CENTER,

   // Widget Type
   WALKTHROUGH;

}
