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
package org.eclipse.osee.ats.util.widgets.dialog;

import org.eclipse.osee.ats.core.task.TaskResOptionDefinition;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class TaskResOptionDefinitionToSwtColor {

   public static int getColorInt(TaskResOptionDefinition def) {
      if (!Strings.isValid(def.getColor())) {
         return SWT.COLOR_BLACK;
      }
      if (def.getColor().equals("WHITE")) {
         return SWT.COLOR_WHITE;
      }
      if (def.getColor().equals("BLACK")) {
         return SWT.COLOR_BLACK;
      }
      if (def.getColor().equals("RED")) {
         return SWT.COLOR_RED;
      }
      if (def.getColor().equals("DARK_RED")) {
         return SWT.COLOR_DARK_RED;
      }
      if (def.getColor().equals("GREEN")) {
         return SWT.COLOR_GREEN;
      }
      if (def.getColor().equals("DARK_GREEN")) {
         return SWT.COLOR_DARK_GREEN;
      }
      if (def.getColor().equals("YELLOW")) {
         return SWT.COLOR_YELLOW;
      }
      if (def.getColor().equals("DARK_YELLOW")) {
         return SWT.COLOR_DARK_YELLOW;
      }
      if (def.getColor().equals("BLUE")) {
         return SWT.COLOR_BLUE;
      }
      if (def.getColor().equals("DARK_BLUE")) {
         return SWT.COLOR_DARK_BLUE;
      }
      if (def.getColor().equals("MAGENTA")) {
         return SWT.COLOR_MAGENTA;
      }
      if (def.getColor().equals("DARK_MAGENTA")) {
         return SWT.COLOR_DARK_MAGENTA;
      }
      if (def.getColor().equals("CYAN")) {
         return SWT.COLOR_CYAN;
      }
      if (def.getColor().equals("DARK_CYAN")) {
         return SWT.COLOR_DARK_CYAN;
      }
      if (def.getColor().equals("GRAY")) {
         return SWT.COLOR_GRAY;
      }
      if (def.getColor().equals("DARK_GRAY")) {
         return SWT.COLOR_DARK_GRAY;
      }
      return SWT.COLOR_BLACK;
   }

}
