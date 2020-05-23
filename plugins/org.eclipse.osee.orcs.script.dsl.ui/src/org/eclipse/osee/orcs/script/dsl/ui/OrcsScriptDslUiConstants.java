/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.orcs.script.dsl.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Roberto E. Escobar
 */
public final class OrcsScriptDslUiConstants {

   private OrcsScriptDslUiConstants() {
      // constants
   }

   private static String asStyleId(String styleName) {
      String normalized = styleName.toLowerCase().replaceAll(" ", ".");
      return String.format("%s.%s.style", STYLE_PREFIX, normalized);
   }

   public static final RGB WHITE_COLOR = new RGB(255, 255, 255);
   public static final RGB BLACK_COLOR = new RGB(0, 0, 0);

   public static final RGB LIME_GREEN_COLOR = new RGB(0, 127, 174);
   public static final RGB LIGHT_BLUE_COLOR = new RGB(127, 159, 191);
   public static final RGB LIGHT_PURPLE_COLOR = new RGB(206, 204, 247);
   public static final RGB TEAL_COLOR = new RGB(68, 103, 161);
   public static final RGB GREEN_COLOR = new RGB(63, 127, 95);
   public static final RGB MAROON_COLOR = new RGB(127, 0, 85);
   public static final RGB LIGHT_GRAY_COLOR = new RGB(100, 100, 100);

   public static final FontData TEXT_FONT = new FontData("Courier New", 10, SWT.NORMAL);
   public static final FontData COMMENT_FONT = new FontData("Segoe Ui", 9, SWT.NORMAL);

   private static final String STYLE_PREFIX = "orcs.script.dsl";

   // provide an id string for the highlighting calculator
   public static final String VARIABLE = "Variable";
   public static final String COMMENT = "Comment";
   public static final String STRING = "String";
   public static final String NUMBER = "Number";
   public static final String BOOLEAN = "Boolean";
   public static final String NULL = "Null";

   public static final String STYLE_ID__VARIABLE = asStyleId(VARIABLE);
   public static final String STYLE_ID__COMMENT = asStyleId(COMMENT);
   public static final String STYLE_ID__STRING = asStyleId(STRING);
   public static final String STYLE_ID__NUMBER = asStyleId(NUMBER);
   public static final String STYLE_ID__BOOLEAN = asStyleId("Boolean");
   public static final String STYLE_ID__NULL = asStyleId("Null");

   public static final RGB COLOR__BACKGROUND = WHITE_COLOR;
   public static final RGB COLOR__FOREGROUND = BLACK_COLOR;
   public static final RGB COLOR__VARIABLE = LIME_GREEN_COLOR;
   public static final RGB COLOR__COMMENT = GREEN_COLOR;
   public static final RGB COLOR__STRING = TEAL_COLOR;
   public static final RGB COLOR__NUMBER = LIGHT_GRAY_COLOR;
   public static final RGB COLOR__BOOLEAN = MAROON_COLOR;
   public static final RGB COLOR__NULL = LIGHT_PURPLE_COLOR;

}
