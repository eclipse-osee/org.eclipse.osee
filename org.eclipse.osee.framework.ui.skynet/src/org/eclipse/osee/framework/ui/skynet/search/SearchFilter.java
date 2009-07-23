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

import static org.eclipse.osee.framework.skynet.core.artifact.search.DeprecatedOperator.EQUAL;
import static org.eclipse.osee.framework.skynet.core.artifact.search.DeprecatedOperator.LIKE;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.skynet.core.artifact.search.DeprecatedOperator;
import org.eclipse.osee.framework.ui.skynet.search.filter.FilterTableViewer;
import org.eclipse.swt.widgets.Control;

/**
 * @author Ryan D. Brooks
 */
public abstract class SearchFilter {
   private static final Pattern wildCardPattern = Pattern.compile("(?<!\\\\)\\*");
   private String filterName;
   protected Control optionsControl;
   protected boolean not;

   public SearchFilter(String filterName, Control optionsControl) {
      this.filterName = filterName;
      this.optionsControl = optionsControl;
      this.not = false;
   }

   public abstract void addFilterTo(FilterTableViewer filterViewer);

   public boolean isValid() {
      return false;
   }

   protected class OperatorAndValue {
      DeprecatedOperator operator;
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

   protected String getFilterName() {
      return filterName;
   }

   public abstract void loadFromStorageString(FilterTableViewer filterViewer, String type, String value, String storageString, boolean isNotEnabled);

}