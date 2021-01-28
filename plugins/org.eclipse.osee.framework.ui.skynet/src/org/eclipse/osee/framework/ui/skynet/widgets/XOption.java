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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.EnumSet;

/**
 * @author Donald G. Dunne
 * @author Karol M. Wilk
 */
public enum XOption {
   NONE("", ""),

   REQUIRED("required", "true"),
   NOT_REQUIRED("required", "false"),
   REQUIRED_FOR_COMPLETION("requiredForCompletion", "true"),
   NOT_REQUIRED_FOR_COMPLETION("requiredForCompletion", "false"),
   ENABLED("enabled", "true"),
   NOT_ENABLED("enabled", "false"),
   EDITABLE("editable", "true"),
   NOT_EDITABLE("editable", "false"),
   MULTI_SELECT("multiSelect", "true"),
   NO_SELECT("noSelect", "false"),
   HORIZONTAL_LABEL("horizontalLabel", "true"),
   VERTICAL_LABEL("horizontalLabel", "false"),
   LABEL_AFTER("labelAfter", "true"),
   LABEL_BEFORE("labelAfter", "false"),
   NO_LABEL("displayLabel", "false"),
   SORTED("", " "),
   ADD_DEFAULT_VALUE("", ""),
   NO_DEFAULT_VALUE("", ""),
   BEGIN_COMPOSITE_4("beginComposite", "4"),
   BEGIN_COMPOSITE_6("beginComposite", "6"),
   BEGIN_COMPOSITE_8("beginComposite", "8"),
   BEGIN_COMPOSITE_10("beginComposite", "10"),
   BEGIN_GROUP_COMPOSITE_4("beginGroupComposite", "4"),
   BEGIN_GROUP_COMPOSITE_6("beginGroupComposite", "6"),
   BEGIN_GROUP_COMPOSITE_8("beginGroupComposite", "8"),
   BEGIN_GROUP_COMPOSITE_10("beginGroupComposite", "10"),
   END_COMPOSITE("endComposite", "true"),

   // Fill Options
   FILL_NONE("", ""),
   FILL_HORIZONTALLY("fill", "Horizontally"),
   FILL_VERTICALLY("fill", "Vertically"),

   // Align Options
   ALIGN_LEFT("align", "Left"),
   ALIGN_RIGHT("align", "Right"),
   ALIGN_CENTER("align", "Center");

   public String keyword;
   public String value;

   public static EnumSet<XOption> enumSet = EnumSet.allOf(XOption.class);

   XOption(String newKeyword, String newValue) {
      keyword = newKeyword;
      value = newValue;
   }
}
