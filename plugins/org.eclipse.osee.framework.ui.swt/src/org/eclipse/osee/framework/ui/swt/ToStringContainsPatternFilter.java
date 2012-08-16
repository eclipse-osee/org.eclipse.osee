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

import java.util.logging.Level;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.internal.Activator;
import org.eclipse.ui.dialogs.PatternFilter;

public class ToStringContainsPatternFilter extends PatternFilter {

   private String text;

   public ToStringContainsPatternFilter() {
      // do nothing
   }

   public void setFilterText(String text) {
      this.text = text;

   }

   @Override
   public void setPattern(String patternString) {
      super.setPattern(patternString);
      if (patternString == null || patternString.isEmpty()) {
         text = null;
      } else {
         text = patternString.toLowerCase();
      }
   }

   @Override
   protected boolean isLeafMatch(Viewer viewer, Object element) {
      if (element == null) {
         return true;
      }
      if (text == null || text.isEmpty()) {
         return true;
      }
      try {
         return element.toString().toLowerCase().contains(text);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return true;
   }

}
