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
package org.eclipse.osee.framework.ui.skynet.widgets.xbargraph;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XBarGraphLine {
   public static int DEFAULT_RED_FOREGROUND = SWT.COLOR_RED;
   public static int DEFAULT_RED_BACKGROUND = SWT.COLOR_YELLOW;
   public static int DEFAULT_GREEN_FOREGROUND = SWT.COLOR_GREEN;
   public static int DEFAULT_GREEN_BACKGROUND = SWT.COLOR_YELLOW;
   public static int DEFAULT_BLUE_FOREGROUND = SWT.COLOR_BLUE;
   public static int DEFAULT_BLUE_BACKGROUND = SWT.COLOR_YELLOW;
   public String name;
   List<XBarGraphLineSegment> segments = new ArrayList<XBarGraphLineSegment>();

   public XBarGraphLine(String name, int value) {
      this(name, DEFAULT_GREEN_FOREGROUND, DEFAULT_GREEN_BACKGROUND, value);
   }

   public XBarGraphLine(String name, int value, String valueStr) {
      this(name, DEFAULT_GREEN_FOREGROUND, DEFAULT_GREEN_BACKGROUND, value, valueStr);
   }

   public XBarGraphLine(String name, List<XBarGraphLineSegment> segments) {
      this.name = name;
      this.segments = segments;
   }

   public XBarGraphLine(String name, int foreground, int background, long value) {
      this(name, foreground, background, value, String.valueOf(value));
   }

   public XBarGraphLine(String name, int foreground, int background, long value, String valueStr) {
      this.name = name;
      segments.add(new XBarGraphLineSegment(valueStr, foreground, background, value));
   }

   public XBarGraphLine(String name, int foreground, int background, int remainingForeground, int remainingBackground, int value, String valueStr) {
      this(name, foreground, background, value, valueStr);
      segments.add(new XBarGraphLineSegment("", 100 - value));
   }

   public XBarGraphLine(String name, int foreground, int background, int remainingForeground, int remainingBackground, int value, String valueStr, String remainingValueStr) {
      this(name, foreground, background, value, valueStr);
      segments.add(new XBarGraphLineSegment(remainingValueStr, remainingForeground, remainingBackground, 100 - value));
   }

   /**
    * Shows as white until completed, then green
    * 
    * @param name
    * @param value
    * @return XBarGraphLine
    */
   public static XBarGraphLine getPercentLine(String name, int value) {
      if (value == 100.0) {
         return new XBarGraphLine(name, DEFAULT_GREEN_FOREGROUND, DEFAULT_GREEN_BACKGROUND, value, value + "%");
      } else {
         return new XBarGraphLine(name, SWT.COLOR_WHITE, SWT.COLOR_WHITE, value, value + "%");
      }
   }

   public static XBarGraphLine getPercentLineBlueGreen(String name, int value) {
      if (value == 100.0) {
         return new XBarGraphLine(name, DEFAULT_GREEN_FOREGROUND, DEFAULT_GREEN_BACKGROUND, value, value + "%");
      } else {
         return new XBarGraphLine(name, SWT.COLOR_YELLOW, SWT.COLOR_GREEN, value, value + "%");
      }
   }

   public static XBarGraphLine getTextLine(String name, String value) {
      return new XBarGraphLine(name, SWT.COLOR_WHITE, SWT.COLOR_WHITE, 0, value);
   }

   public static XBarGraphLine getTextLineRedIfTrue(String name, String value, boolean isRed) {
      return new XBarGraphLine(name, isRed ? DEFAULT_RED_FOREGROUND : SWT.COLOR_WHITE,
            isRed ? DEFAULT_RED_BACKGROUND : SWT.COLOR_WHITE, isRed ? 100 : 0, value);
   }
}
