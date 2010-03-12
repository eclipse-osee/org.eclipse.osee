/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

/**
 * @author Donald G. Dunne
 */
public enum XOption {
   NONE,

   REQUIRED,
   NOT_REQUIRED,
   ENABLED,
   NOT_ENABLED,
   EDITABLE,
   NOT_EDITABLE,
   MULTI_SELECT,
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
   ALIGN_CENTER;

}
