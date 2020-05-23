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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.ui.dialogs.PatternFilter;

public class XNavigateViewFilter extends PatternFilter {

   private String text;

   public XNavigateViewFilter(String filterText) {
      if (Strings.isValid(filterText)) {
         setPattern(filterText);
      }
   }

   @Override
   public void setPattern(String patternString) {
      super.setPattern(patternString);
      if (Strings.isValid(patternString)) {
         text = patternString.toLowerCase();
      }
   }

   @Override
   protected boolean isLeafMatch(Viewer viewer, Object element) {
      if (element == null) {
         return true;
      }
      if (!Strings.isValid(text)) {
         return true;
      }
      return element.toString().toLowerCase().contains(text);
   }

}
