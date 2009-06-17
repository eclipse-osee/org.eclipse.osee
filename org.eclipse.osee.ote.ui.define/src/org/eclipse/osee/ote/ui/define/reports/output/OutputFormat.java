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
package org.eclipse.osee.ote.ui.define.reports.output;

import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public enum OutputFormat {

   HTML, PDF, RTF, EXCEL;

   public static OutputFormat fromString(String value) {
      OutputFormat toReturn = OutputFormat.HTML;
      if (Strings.isValid(value)) {
         try {
            toReturn = OutputFormat.valueOf(value.toUpperCase());
         } catch (Exception ex) {
            toReturn = OutputFormat.HTML;
         }
      }
      return toReturn;
   }
}
