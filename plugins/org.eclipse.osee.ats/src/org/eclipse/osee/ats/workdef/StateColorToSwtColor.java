/*
 * Created on May 4, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import org.eclipse.osee.ats.core.workdef.StateColor;
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
