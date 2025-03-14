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

package org.eclipse.osee.framework.ui.swt;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * Share font resources among OSEE applications
 *
 * @author Donald G. Dunne
 */
public final class FontManager {

   public static Map<String, Font> fontMap = new HashMap<>();
   private static Color LIGHT_RED, DARK_CYAN, DARK_GREEN, DARK_YELLOW, DARK_BLUE;

   private FontManager() {
      // Utility Class
   }

   public static Font getDefaultLabelFont() {
      return getFont("arial", 12, SWT.BOLD);
   }

   public static Font getCourierNew8() {
      return getFont("Courier New", 8, SWT.NONE);
   }

   public static Font getCourierNew12Bold() {
      return getFont("Courier New", 12, SWT.BOLD);
   }

   public static Font getCourierNew12() {
      return getFont("Courier New", 12, SWT.NONE);
   }

   public static Font getFont(String fontName, int size, int swtType) {
      String hashKey = fontName + "-" + size + "-" + swtType;
      if (!fontMap.containsKey(hashKey)) {
         Font baseFont = JFaceResources.getDefaultFont();
         FontData[] fontDatas = baseFont.getFontData();
         FontData fontData = fontDatas.length > 0 ? fontDatas[0] : new FontData(fontName, size, swtType);
         fontMap.put(hashKey, new Font(baseFont.getDevice(), fontData.getName(), fontData.getHeight(), SWT.BOLD));
      }
      return fontMap.get(hashKey);
   }

   public static Color getLightRed() {
      if (LIGHT_RED == null) {
         LIGHT_RED = Displays.getColor(255, 108, 113);
      }
      return LIGHT_RED;
   }

   public static Color getDarkCyan() {
      if (DARK_CYAN == null) {
         DARK_CYAN = Displays.getSystemColor(SWT.COLOR_DARK_CYAN);
      }
      return DARK_CYAN;
   }

   public static Color getDarkBlue() {
      if (DARK_BLUE == null) {
         DARK_BLUE = Displays.getSystemColor(SWT.COLOR_DARK_BLUE);
      }
      return DARK_BLUE;
   }

   public static Color getDarkGreen() {
      if (DARK_GREEN == null) {
         DARK_GREEN = Displays.getSystemColor(SWT.COLOR_DARK_GREEN);
      }
      return DARK_GREEN;
   }

   public static Color getDarkYellow() {
      if (DARK_YELLOW == null) {
         DARK_YELLOW = Displays.getSystemColor(SWT.COLOR_DARK_YELLOW);
      }
      return DARK_YELLOW;
   }

}
