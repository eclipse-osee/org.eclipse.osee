/*
 * Created on Jul 10, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

/**
 * Share font resources among OSEE applications
 * 
 * @author Donald G. Dunne
 */
public class FontManager {

   public static Map<String, Font> fontMap = new HashMap<String, Font>();

   public static Font getDefaultLabelFont() {
      return getFont("arial", 12, SWT.BOLD);
   }

   public static Font getCourierNew8() {
      return getFont("Courier New", 8, SWT.NONE);
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

}
