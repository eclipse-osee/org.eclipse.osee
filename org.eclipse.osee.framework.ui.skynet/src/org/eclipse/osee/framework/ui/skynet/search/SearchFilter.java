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
package org.eclipse.osee.framework.ui.skynet.search;

import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.EQUAL;
import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.LIKE;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.skynet.core.artifact.search.Operator;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public class SearchFilter {
   private static final Pattern wildCardPattern = Pattern.compile("(?<!\\\\)\\*");
   protected String filterName;
   protected Control optionsControl;
   protected boolean not;

   public SearchFilter(String filterName, Control optionsControl) {
      this.filterName = filterName;
      this.optionsControl = optionsControl;
      this.not = false;
   }

   public void addFilterTo(FilterTableViewer filterViewer) {

   }

   public boolean isValid() {
      return false;
   }

   protected class OperatorAndValue {
      Operator operator;
      String value;
   }

   public OperatorAndValue handleWildCard(String value) {
      OperatorAndValue result = new OperatorAndValue();
      Matcher wildCardMatcher = wildCardPattern.matcher(value);
      if (wildCardMatcher.find()) {
         wildCardMatcher.reset();
         value = wildCardMatcher.replaceAll("%");
         result.operator = LIKE;
      } else {
         result.operator = EQUAL;
      }
      result.value = value.replaceAll("\\\\\\*", "*");
      return result;
   }

   /**
    * @return Returns the not.
    */
   public boolean isNot() {
      return not;
   }

   /**
    * @param not The not to set.
    */
   public void setNot(boolean not) {
      this.not = not;
   }
}