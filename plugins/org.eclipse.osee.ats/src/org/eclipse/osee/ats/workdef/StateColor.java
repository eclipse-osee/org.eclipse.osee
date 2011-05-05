/*
 * Created on May 4, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.workdef;

import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public enum StateColor {
   BLACK(SWT.COLOR_BLACK),
   WHITE(SWT.COLOR_WHITE),
   RED(SWT.COLOR_RED),
   DARK_RED(SWT.COLOR_DARK_RED),
   GREEN(SWT.COLOR_GREEN),
   DARK_GREEN(SWT.COLOR_DARK_GREEN),
   YELLOW(SWT.COLOR_YELLOW),
   DARK_YELLOW(SWT.COLOR_DARK_YELLOW),
   BLUE(SWT.COLOR_BLUE),
   DARK_BLUE(SWT.COLOR_DARK_BLUE),
   MAGENTA(SWT.COLOR_MAGENTA),
   DARK_MAGENTA(SWT.COLOR_DARK_MAGENTA),
   CYAN(SWT.COLOR_CYAN),
   DARK_CYAN(SWT.COLOR_DARK_CYAN),
   GRAY(SWT.COLOR_GRAY),
   DARK_GRAY(SWT.COLOR_DARK_GRAY);

   private final int swtColorId;

   private StateColor(int swtColorId) {
      this.swtColorId = swtColorId;
   }

   public int getSwtColorId() {
      return swtColorId;
   }
}
