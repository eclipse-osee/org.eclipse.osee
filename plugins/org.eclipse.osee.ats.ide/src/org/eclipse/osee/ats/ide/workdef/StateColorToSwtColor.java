/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workdef;

import org.eclipse.osee.ats.api.workdef.StateColor;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class StateColorToSwtColor {

   public static int convert(StateColor color) {
      if (color == StateColor.BLACK) {
         return SWT.COLOR_BLACK;
      }
      if (color == StateColor.WHITE) {
         return SWT.COLOR_WHITE;
      }
      if (color == StateColor.RED) {
         return SWT.COLOR_RED;
      }
      if (color == StateColor.DARK_RED) {
         return SWT.COLOR_DARK_RED;
      }
      if (color == StateColor.GREEN) {
         return SWT.COLOR_GREEN;
      }
      if (color == StateColor.DARK_GREEN) {
         return SWT.COLOR_DARK_GREEN;
      }
      if (color == StateColor.YELLOW) {
         return SWT.COLOR_YELLOW;
      }
      if (color == StateColor.DARK_YELLOW) {
         return SWT.COLOR_DARK_YELLOW;
      }
      if (color == StateColor.BLUE) {
         return SWT.COLOR_BLUE;
      }
      if (color == StateColor.DARK_BLUE) {
         return SWT.COLOR_DARK_BLUE;
      }
      if (color == StateColor.MAGENTA) {
         return SWT.COLOR_MAGENTA;
      }
      if (color == StateColor.DARK_MAGENTA) {
         return SWT.COLOR_DARK_MAGENTA;
      }
      if (color == StateColor.CYAN) {
         return SWT.COLOR_CYAN;
      }
      if (color == StateColor.DARK_CYAN) {
         return SWT.COLOR_DARK_CYAN;
      }
      if (color == StateColor.GRAY) {
         return SWT.COLOR_GRAY;
      }
      if (color == StateColor.DARK_GRAY) {
         return SWT.COLOR_DARK_GRAY;
      }
      return SWT.COLOR_BLACK;
   }
}
