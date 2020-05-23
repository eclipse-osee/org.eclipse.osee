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

package org.eclipse.osee.framework.ui.swt;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.swt.internal.Activator;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * Matches lowercase label provider text (if valid), else matches lowercase toString.
 *
 * @author Donald G. Dunne
 */
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
         if (viewer instanceof ContentViewer && ((ContentViewer) viewer).getLabelProvider() instanceof ILabelProvider) {
            String value = ((ILabelProvider) ((ContentViewer) viewer).getLabelProvider()).getText(element);
            if (value == null) {
               value = "";
            }
            return value.toLowerCase().contains(text);
         } else {
            return element.toString().toLowerCase().contains(text);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return true;
   }

}
