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
package org.eclipse.osee.framework.ui.swt;

import java.net.InetAddress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This is a custom widget that allows for a user to enter an IP Address (or net mask).
 * 
 * @author Ken J. Aguilar
 */
public class IPAddressBox extends Composite {
   private static final String PERIOD_SEPARATOR = ".";
   private static final String DASH_SEPARATOR = "-";
   private static final int NUM_BOXES = 4;

   private final Text[] box = new Text[NUM_BOXES];
   private Color background;

   public IPAddressBox(final Composite parent) {
      this(parent, true, SWT.BORDER);
   }

   public IPAddressBox(final Composite parent, boolean useDash, int style) {
      super(parent, style);
      Widgets.setFormLayout(this, 0, 0);

      String separator = (useDash ? DASH_SEPARATOR : PERIOD_SEPARATOR);
      if ((style & SWT.READ_ONLY) > 0) {
         background = parent.getBackground();
      } else {
         background = getDisplay().getSystemColor(SWT.COLOR_WHITE);
      }

      setBackground(background);

      int NO_BORDER_MASK = ~SWT.BORDER;

      Label sep1 = new Label(this, SWT.CENTER);
      sep1.setText(separator);
      sep1.setBackground(background);
      Widgets.attachToParent(sep1, SWT.LEFT, 25, 0);
      Widgets.attachToParent(sep1, SWT.TOP, 0, 3);

      Label sep2 = new Label(this, SWT.CENTER);
      sep2.setText(separator);
      sep2.setBackground(background);
      Widgets.attachToParent(sep2, SWT.LEFT, 50, -2);
      Widgets.attachToControl(sep2, sep1, SWT.TOP, SWT.TOP, 0);

      Label sep3 = new Label(this, SWT.CENTER);
      sep3.setText(separator);
      sep3.setBackground(background);
      Widgets.attachToParent(sep3, SWT.LEFT, 75, -2);
      Widgets.attachToControl(sep3, sep1, SWT.TOP, SWT.TOP, 0);

      box[0] = new Text(this, SWT.SINGLE | SWT.RIGHT | style & NO_BORDER_MASK);
      box[0].setTextLimit(3);
      box[0].setBackground(background);
      new Widgets.IntegerTextEntryHandler(box[0], false, 3) {
         @Override
         public void applyValue(long value) {
            clamp(box[0], value);
         }
      };
      Widgets.attachToParent(box[0], SWT.LEFT, 0, 2);
      Widgets.attachToControl(box[0], sep1, SWT.RIGHT, SWT.LEFT, -2);
      Widgets.attachToControl(box[0], sep1, SWT.TOP, SWT.CENTER, 0);

      box[1] = new Text(this, SWT.SINGLE | SWT.RIGHT | style & NO_BORDER_MASK);
      box[1].setTextLimit(3);
      box[1].setBackground(background);
      new Widgets.IntegerTextEntryHandler(box[1], false, 3) {
         @Override
         public void applyValue(long value) {
            clamp(box[1], value);
         }
      };
      Widgets.attachToControl(box[1], sep1, SWT.LEFT, SWT.RIGHT, 2);
      Widgets.attachToControl(box[1], sep2, SWT.RIGHT, SWT.LEFT, -2);
      Widgets.attachToControl(box[1], sep1, SWT.TOP, SWT.CENTER, 0);

      box[2] = new Text(this, SWT.SINGLE | SWT.RIGHT | style & NO_BORDER_MASK);
      box[2].setTextLimit(3);
      box[2].setBackground(background);
      new Widgets.IntegerTextEntryHandler(box[2], false, 3) {
         @Override
         public void applyValue(long value) {
            clamp(box[2], value);
         }
      };
      Widgets.attachToControl(box[2], sep2, SWT.LEFT, SWT.RIGHT, 2);
      Widgets.attachToControl(box[2], sep3, SWT.RIGHT, SWT.LEFT, -2);
      Widgets.attachToControl(box[2], sep1, SWT.TOP, SWT.CENTER, 0);

      box[3] = new Text(this, SWT.SINGLE | SWT.RIGHT | style & NO_BORDER_MASK);
      box[3].setTextLimit(3);
      box[3].setBackground(background);
      new Widgets.IntegerTextEntryHandler(box[3], false, 3) {
         @Override
         public void applyValue(long value) {
            clamp(box[3], value);
         }
      };
      Widgets.attachToParent(box[3], SWT.RIGHT, 100, -2);
      Widgets.attachToControl(box[3], sep3, SWT.LEFT, SWT.RIGHT, 2);
      Widgets.attachToControl(box[3], sep1, SWT.TOP, SWT.CENTER, 0);
   }

   /**
    * Creates a new IPAddressBox
    * 
    * @param parent The parent to which this IPAddressBox will be a child of
    */
   public IPAddressBox(final Composite parent, boolean useDash) {
      this(parent, useDash, SWT.BORDER);
   }

   private void clamp(Text txt, long value) {
      if (value < 0) {
         txt.setText(Integer.toString(0));
      }
      if (value > 255) {
         txt.setText(Integer.toString(255));
      }
   }

   @Override
   public void setBackground(final Color color) {
      super.setBackground(color);
      for (Control ctrl : getChildren()) {
         ctrl.setBackground(color);
      }
      background = color;
   }

   @Override
   public void setForeground(final Color color) {
      super.setForeground(color);
      for (Control ctrl : getChildren()) {
         ctrl.setForeground(color);
      }
   }

   @Override
   public Color getBackground() {
      return background;
   }

   public int getIntValue() {
      final String txt0 = box[0].getText();
      final String txt1 = box[1].getText();
      final String txt2 = box[2].getText();
      final String txt3 = box[3].getText();

      // System.out.println("get ip addr box value");
      final int a = (txt0.equals("") ? 0 : (Integer.parseInt(txt0) & 0x000000FF));
      // System.out.println("a = " + a);
      // System.out.println(Integer.toBinaryString(a));
      final int b = (txt1.equals("") ? 0 : (Integer.parseInt(txt1) & 0x000000FF));
      // System.out.println("b = " + b);
      // System.out.println(Integer.toBinaryString(b));
      final int c = (txt2.equals("") ? 0 : (Integer.parseInt(txt2) & 0x000000FF));
      // System.out.println("c = " + c);
      // System.out.println(Integer.toBinaryString(c));
      final int d = (txt3.equals("") ? 0 : (Integer.parseInt(txt3) & 0x000000FF));
      // System.out.println("d = " + d);
      // System.out.println(Integer.toBinaryString(d));
      return (a << 24) | (b << 16) | (c << 8) | d;
   }

   /**
    * Sets the ip address when given a string that is formatted as follows
    * <P>
    * <I> ###.###.###.### or as ###-###-###-### where ### is an integer between 0 and 255 inclusive. </I>
    * 
    * @param txt
    */
   public void setTextValue(final String txt) {
      final String[] bytes = txt.split("\\.|-");
      if (bytes.length != 4) {
         throw new IllegalArgumentException("Invalid IP Address format: " + txt);
      }

      for (final String bytestring : bytes) {
         final int value = Integer.parseInt(bytestring);
         if (value < 0 || value > 255) {
            throw new IllegalArgumentException("Invalid IP Address format: " + txt);
         }
      }
      box[0].setText(bytes[0]);
      box[1].setText(bytes[1]);
      box[2].setText(bytes[2]);
      box[3].setText(bytes[3]);
   }

   /**
    * sets the ip address when given an integer that represents 4 8-bit values. the leftmost 8 bits of the integer
    * represent the first byte of the ip address.
    * 
    * @param value
    */
   public void setIntValue(final int value) {
      // System.out.println("set ip addr box value = " + value);
      final int a = (value & 0xFF000000) >>> 24;
      // System.out.println("a = " + a);
      // System.out.println(Integer.toBinaryString(a));
      final int b = (value & 0x00FF0000) >> 16;
      // System.out.println("b = " + b);
      // System.out.println(Integer.toBinaryString(b));
      final int c = (value & 0x0000FF00) >> 8;
      // System.out.println("c = " + c);
      // System.out.println(Integer.toBinaryString(c));
      final int d = (value & 0x000000FF);
      // System.out.println("d = " + d);
      // System.out.println(Integer.toBinaryString(d));
      box[0].setText(Integer.toString(a));
      box[1].setText(Integer.toString(b));
      box[2].setText(Integer.toString(c));
      box[3].setText(Integer.toString(d));

   }

   public static byte[] getByteArray(int value) {
      final int a = (value & 0xFF000000) >>> 24;
      final int b = (value & 0x00FF0000) >> 16;
      final int c = (value & 0x0000FF00) >> 8;
      final int d = (value & 0x000000FF);
      return new byte[] {(byte) a, (byte) b, (byte) c, (byte) d};
   }

   public static int getInetAddressToInt(InetAddress address) {
      byte[] bytes = address.getAddress();

      return ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
   }

   public static String toString(final int value) {
      final int a = (value & 0xFF000000) >>> 24;
      final int b = (value & 0x00FF0000) >> 16;
      final int c = (value & 0x0000FF00) >> 8;
      final int d = (value & 0x000000FF);
      return String.format("%03d.%03d.%03d.%03d", a, b, c, d);
   }

}
