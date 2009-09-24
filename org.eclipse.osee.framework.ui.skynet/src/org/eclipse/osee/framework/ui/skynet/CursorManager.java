/*
 * Created on Sep 24, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class CursorManager {

   private static final Map<Integer, Cursor> cursors = new HashMap<Integer, Cursor>();

   /**
    * @param style SWT.CURSOR_*
    * @return shared cursor resource that does not need to be destroyed
    */
   public static Cursor getCursor(int style) {
      if (cursors.get(style) == null) {
         cursors.put(style, new Cursor(Display.getCurrent(), style));
      }
      return cursors.get(style);
   }
}
