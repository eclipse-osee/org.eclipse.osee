/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.widgets.xbargraph;

import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XBarGraphLineSegment {
   public static final int DEFAULT_RED_FOREGROUND = SWT.COLOR_RED;
   public static final int DEFAULT_RED_BACKGROUND = SWT.COLOR_YELLOW;
   public static final int DEFAULT_GREEN_FOREGROUND = SWT.COLOR_GREEN;
   public static final int DEFAULT_GREEN_BACKGROUND = SWT.COLOR_YELLOW;

   private final String name;
   private final int foreground;
   private final int background;
   private final long value;

   public XBarGraphLineSegment(String name, long value) {
      this(name, DEFAULT_GREEN_FOREGROUND, DEFAULT_GREEN_BACKGROUND, value);
   }

   public XBarGraphLineSegment(String name, int color, long value) {
      this(name, color, color, value);
   }

   public XBarGraphLineSegment(String name, int foreground, int background, long value) {
      this.name = name;
      this.foreground = foreground;
      this.background = background;
      this.value = value;
   }

   public static XBarGraphLineSegment getPercentSegment(String name, long value) {
      return new XBarGraphLineSegment(value + "%", DEFAULT_GREEN_FOREGROUND, DEFAULT_GREEN_BACKGROUND, value);
   }

   public static XBarGraphLineSegment getPercentSegment(String name, int color, long value) {
      return new XBarGraphLineSegment(value + "%", color, color, value);
   }

   public String getName() {
      return name;
   }

   public int getForeground() {
      return foreground;
   }

   public int getBackground() {
      return background;
   }

   public long getValue() {
      return value;
   }

}
