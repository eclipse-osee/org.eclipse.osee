package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class StyledTextExample {
   Color orange, blue, red, green;

   public void run() {
      Display display = new Display();
      Shell shell = new Shell(display);

      orange = new Color(display, 255, 127, 0);
      blue = display.getSystemColor(SWT.COLOR_BLUE);
      red = display.getSystemColor(SWT.COLOR_RED);
      green = display.getSystemColor(SWT.COLOR_GREEN);

      create(shell);
      shell.setSize(430, 100);
      shell.setText("Styled Text");
      shell.open();
      while (!shell.isDisposed()) {
         if (!display.readAndDispatch()) {
            display.sleep();
         }
      }
      display.dispose();
   }

   private void create(final Shell shell) {
      shell.setLayout(new FillLayout());
      final StyledText styledText = new StyledText(shell, SWT.BORDER);
      styledText.setText("Java is an Object Oriented Programming Language.");
      Font font = new Font(shell.getDisplay(), "Courier", 10, SWT.NORMAL);
      styledText.setFont(font);

      StyleRange[] ranges = new StyleRange[7];
      ranges[0] = new StyleRange(0, 4, orange, null);
      ranges[0].underline = true;
      ranges[0].background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
      ranges[0].fontStyle = SWT.BOLD;

      ranges[1] = new StyleRange(5, 2, blue, null);
      ranges[1].background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);

      ranges[2] = new StyleRange(8, 2, red, null);
      ranges[2].background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);

      ranges[3] = new StyleRange(11, 6, green, null);
      ranges[3].underline = true;
      ranges[3].background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
      ranges[3].fontStyle = SWT.BOLD;

      ranges[4] = new StyleRange(18, 8, orange, null);
      ranges[4].underline = true;
      ranges[4].background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
      ranges[4].fontStyle = SWT.BOLD;

      ranges[5] = new StyleRange(27, 11, blue, null);
      ranges[5].background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);

      ranges[6] = new StyleRange(39, 9, red, null);
      ranges[6].background = shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW);
      styledText.replaceStyleRanges(0, 48, ranges);
   }

   public static void main(String[] args) {
      new StyledTextExample().run();
   }
}