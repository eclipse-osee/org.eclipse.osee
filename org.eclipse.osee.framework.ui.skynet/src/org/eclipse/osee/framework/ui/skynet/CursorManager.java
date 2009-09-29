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
