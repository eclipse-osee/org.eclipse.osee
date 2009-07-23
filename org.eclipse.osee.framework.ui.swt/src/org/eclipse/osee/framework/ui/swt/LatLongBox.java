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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * This is a custom widget that handles latitude and longitude data entries in the form of degrees, minutes, and seconds
 * 
 * @author Ken J. Aguilar
 */
public class LatLongBox extends Composite {

   private final Text degreesTxt;
   private final Text minutesTxt;
   private final Text secondsTxt;

   private Color background;

   private boolean hasFocus = false;
   private boolean[] boxTabFlag = new boolean[] {false, false, false};

   private int maxDegrees = 360;
   private int minDegrees = -360;

   private Double dValue = null;

   public LatLongBox(final Composite parent) {
      this(parent, SWT.BORDER);
   }

   public LatLongBox(final Composite parent, int style) {
      super(parent, style);
      setToolTipText("Enter degrees, minutes, and seconds");
      Widgets.setFormLayout(this, 0, 0);

      if ((style & SWT.READ_ONLY) > 0) {
         background = parent.getBackground();
      } else {
         background = getDisplay().getSystemColor(SWT.COLOR_WHITE);
      }

      setBackground(background);

      int NO_BORDER_MASK = ~SWT.BORDER;

      Label sep1 = new Label(this, SWT.CENTER);
      sep1.setText(String.format("%c", 0xb0));
      sep1.setBackground(background);
      Widgets.attachToParent(sep1, SWT.LEFT, 33, 0);
      Widgets.attachToParent(sep1, SWT.TOP, 0, 3);

      Label sep2 = new Label(this, SWT.CENTER);
      sep2.setText("'");
      sep2.setBackground(background);
      Widgets.attachToParent(sep2, SWT.LEFT, 66, -2);
      Widgets.attachToControl(sep2, sep1, SWT.TOP, SWT.TOP, 0);

      Label sep3 = new Label(this, SWT.CENTER);
      sep3.setText("\"");
      sep3.setBackground(background);
      Widgets.attachToParent(sep3, SWT.LEFT, 100, -8);
      Widgets.attachToControl(sep3, sep1, SWT.TOP, SWT.TOP, 0);

      degreesTxt = new Text(this, SWT.SINGLE | SWT.RIGHT | style & NO_BORDER_MASK);
      degreesTxt.setTextLimit(4);
      degreesTxt.setBackground(background);
      Widgets.attachToParent(degreesTxt, SWT.LEFT, 0, 2);
      Widgets.attachToControl(degreesTxt, sep1, SWT.RIGHT, SWT.LEFT, -2);
      Widgets.attachToControl(degreesTxt, sep1, SWT.TOP, SWT.CENTER, 0);

      minutesTxt = new Text(this, SWT.SINGLE | SWT.RIGHT | style & NO_BORDER_MASK);
      minutesTxt.setTextLimit(2);
      minutesTxt.setBackground(background);
      Widgets.attachToControl(minutesTxt, sep1, SWT.LEFT, SWT.RIGHT, 2);
      Widgets.attachToControl(minutesTxt, sep2, SWT.RIGHT, SWT.LEFT, -2);
      Widgets.attachToControl(minutesTxt, sep1, SWT.TOP, SWT.CENTER, 0);

      secondsTxt = new Text(this, SWT.SINGLE | SWT.RIGHT | style & NO_BORDER_MASK);
      secondsTxt.setTextLimit(2);
      secondsTxt.setBackground(background);

      Widgets.attachToControl(secondsTxt, sep2, SWT.LEFT, SWT.RIGHT, 2);
      Widgets.attachToControl(secondsTxt, sep3, SWT.RIGHT, SWT.LEFT, -2);
      Widgets.attachToControl(secondsTxt, sep1, SWT.TOP, SWT.CENTER, 0);

      new Widgets.IntegerTextEntryHandler(degreesTxt, true, 4) {
         @Override
         public void keyTraversed(TraverseEvent e) {
            try {
               super.keyTraversed(e);
               switch (e.detail) {
                  case SWT.TRAVERSE_ARROW_NEXT:
                     if (degreesTxt.getCaretPosition() == degreesTxt.getCharCount()) {
                        boxTabFlag[0] = false;
                        minutesTxt.setFocus();
                     }
                     break;
                  case SWT.TRAVERSE_TAB_NEXT:
                     boxTabFlag[0] = false;
                     break;
                  case SWT.TRAVERSE_TAB_PREVIOUS:
                     boxTabFlag[0] = true;
                     break;
               }
            } catch (RuntimeException e1) {
               e1.printStackTrace();
            }
         }

         @Override
         public void focusGained(FocusEvent e) {
            super.focusGained(e);
            boxTabFlag[0] = true;
            if (!hasFocus) {
               hasFocus = true;
               generateCompositeFocusEvent(e, SWT.FocusIn);
            }
         }

         @Override
         public void focusLost(FocusEvent e) {
            try {
               super.focusLost(e);
               Control control = Display.getDefault().getCursorControl();
               if (control != minutesTxt && control != secondsTxt && boxTabFlag[0]) {
                  // the latLongBox has lost focus. None of its Text fields have the focus
                  hasFocus = false;
                  generateCompositeFocusEvent(e, SWT.FocusOut);
               }
            } catch (RuntimeException e1) {
               e1.printStackTrace();
            }
         }

         @Override
         public void applyValue(long value) {
            try {
               if (value > maxDegrees) {
                  value = maxDegrees;
                  degreesTxt.setText(Integer.toString(maxDegrees));
               } else if (value < minDegrees) {
                  value = minDegrees;
                  degreesTxt.setText(Integer.toString(minDegrees));
               }
               if (value == maxDegrees) {
                  minutesTxt.setText("0");
                  secondsTxt.setText("0");
               }
               if (minutesTxt.getText().equals("")) {
                  minutesTxt.setText("0");
               }
               if (secondsTxt.getText().equals("")) {
                  secondsTxt.setText("0");
               }
               Long prevValue = getPreviousValue();
               if (prevValue == null) {
                  prevValue = Long.MIN_VALUE;
               }
               if (value != prevValue.longValue()) {
                  double sign = value < 0 ? -1.0 : 1.0;
                  dValue =
                        (double) value + sign * (Integer.parseInt(minutesTxt.getText()) / 60.0 + Integer.parseInt(secondsTxt.getText()) / 3600.0);
                  System.out.println("latlong degrees changed: degrees = " + value + ", dValue=" + dValue);
               }
            } catch (Throwable t) {
               t.printStackTrace();
            }
         }
      };

      new Widgets.IntegerTextEntryHandler(minutesTxt, false, 2) {
         @Override
         public void keyTraversed(TraverseEvent e) {
            try {
               super.keyTraversed(e);
               switch (e.detail) {
                  case SWT.TRAVERSE_ARROW_NEXT:
                     if (minutesTxt.getCaretPosition() == minutesTxt.getCharCount()) {
                        boxTabFlag[1] = false;
                        secondsTxt.setFocus();
                     }
                     break;
                  case SWT.TRAVERSE_ARROW_PREVIOUS:
                     if (minutesTxt.getCaretPosition() == 0) {
                        degreesTxt.setFocus();
                     }
                     boxTabFlag[1] = false;
                  case SWT.TRAVERSE_TAB_NEXT:
                     boxTabFlag[1] = false;
                     break;
                  case SWT.TRAVERSE_TAB_PREVIOUS:
                     boxTabFlag[1] = false;
                     break;
               }
            } catch (RuntimeException e1) {
               e1.printStackTrace();
            }
         }

         @Override
         public void focusGained(FocusEvent e) {
            super.focusGained(e);
            boxTabFlag[1] = true;
            if (!hasFocus) {
               hasFocus = true;
               generateCompositeFocusEvent(e, SWT.FocusIn);
            }
         }

         @Override
         public void focusLost(FocusEvent e) {
            try {
               super.focusLost(e);
               Control control = Display.getDefault().getCursorControl();
               if (control != degreesTxt && control != secondsTxt && boxTabFlag[1]) {
                  // the latLongBox has lost focus. None of its Text fields have the focus
                  hasFocus = false;
                  generateCompositeFocusEvent(e, SWT.FocusOut);
               }
            } catch (RuntimeException e1) {
               e1.printStackTrace();
            }
         }

         @Override
         public void applyValue(long value) {
            try {
               int degrees;
               String dTxt = degreesTxt.getText();
               if (dTxt.equals("")) {
                  degrees = 0;
                  degreesTxt.setText("0");
               } else {
                  degrees = Integer.parseInt(dTxt);
               }
               if (degrees == maxDegrees) {
                  value = 0;
               } else if (value > 59) {
                  value = 59;
                  minutesTxt.setText(Integer.toString(59));
               }
               Long prevValue = getPreviousValue();
               if (prevValue == null) {
                  prevValue = Long.MIN_VALUE;
               }
               if (value != prevValue) {
                  double sign = degrees < 0 ? -1.0 : 1.0;
                  String sTxt = secondsTxt.getText();
                  if (sTxt.equals("")) {
                     dValue = (double) degrees + sign * value / 60.0;
                     secondsTxt.setText("0");
                  } else {
                     dValue = (double) degrees + sign * (value / 60.0 + Integer.parseInt(sTxt) / 3600.0);
                  }
                  System.out.println("latlong minutes changed: minutes = " + value + ", dValue=" + dValue);
               }
            } catch (RuntimeException e) {
               e.printStackTrace();
            }
         }
      };

      new Widgets.IntegerTextEntryHandler(secondsTxt, false, 2) {
         @Override
         public void keyTraversed(TraverseEvent e) {
            super.keyTraversed(e);
            switch (e.detail) {
               case SWT.TRAVERSE_ARROW_PREVIOUS:
                  if (secondsTxt.getCaretPosition() == 0) {
                     boxTabFlag[2] = false;
                     minutesTxt.setFocus();
                  }
               case SWT.TRAVERSE_TAB_NEXT:
                  boxTabFlag[2] = true;
                  break;
               case SWT.TRAVERSE_TAB_PREVIOUS:
                  boxTabFlag[2] = false;
                  break;
            }
         }

         @Override
         public void focusGained(FocusEvent e) {
            super.focusGained(e);
            boxTabFlag[2] = true;
            if (!hasFocus) {
               hasFocus = true;
               generateCompositeFocusEvent(e, SWT.FocusIn);
            }
         }

         @Override
         public void focusLost(FocusEvent e) {
            try {
               super.focusLost(e);
               Control control = Display.getDefault().getCursorControl();
               if (control != minutesTxt && control != degreesTxt && boxTabFlag[2]) {
                  // the latLongBox has lost focus. None of its Text fields have the focus
                  hasFocus = false;
                  generateCompositeFocusEvent(e, SWT.FocusOut);
               }
            } catch (RuntimeException e1) {
               e1.printStackTrace();
            }
         }

         @Override
         public void applyValue(long value) {
            try {
               int degrees;
               String dTxt = degreesTxt.getText();
               if (dTxt.equals("")) {
                  degrees = 0;
                  degreesTxt.setText("0");
               } else {
                  degrees = Integer.parseInt(dTxt);
               }
               if (degrees == maxDegrees) {
                  value = 0;
               } else if (value > 59) {
                  value = 59;
                  secondsTxt.setText(Integer.toString(59));
               }
               Long prevValue = getPreviousValue();
               if (prevValue == null) {
                  prevValue = Long.MIN_VALUE;
               }
               if (value != prevValue) {
                  double sign = degrees < 0 ? -1.0 : 1.0;
                  String mTxt = minutesTxt.getText();
                  if (mTxt.equals("")) {
                     dValue = (double) degrees + sign * value / 3600.0;
                     minutesTxt.setText("0");
                  } else {
                     dValue = (double) degrees + sign * (Integer.parseInt(mTxt) / 60.0 + value / 3600.0);
                  }
                  System.out.println("latlong seconds changed: seconds = " + value + ", dValue=" + dValue);
               }
            } catch (RuntimeException e) {
               e.printStackTrace();
            }
         }
      };
   }

   private void generateCompositeFocusEvent(FocusEvent e, int type) {
      final Event event = new Event();
      event.display = e.display;
      event.time = e.time;
      event.data = e.data;
      event.widget = LatLongBox.this;
      event.type = type;
      notifyListeners(type, event);
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

   /**
    * returns the value in degrees with a fractional part calculated from minutes and seconds. For example 50 degrees
    * and 30 minutes would return 50.5 degrees
    */
   public Double getValue() {
      return dValue;
   }

   public int getMinutes() {
      return Integer.parseInt(minutesTxt.getText());
   }

   public int getSeconds() {
      return Integer.parseInt(secondsTxt.getText());
   }

   @Override
   public Color getBackground() {
      return background;
   }

   public int getMaxDegrees() {
      return maxDegrees;
   }

   public void setMaxDegrees(int maxDegrees) {
      this.maxDegrees = maxDegrees;
   }

   public int getMinDegrees() {
      return minDegrees;
   }

   public void setMinDegrees(int minDegrees) {
      this.minDegrees = minDegrees;
   }

   public String toString() {
      return String.format(degreesTxt.getText() + (char) 0xb0 + minutesTxt.getText() + "' " + secondsTxt.getText() + "\"");
   }

}
