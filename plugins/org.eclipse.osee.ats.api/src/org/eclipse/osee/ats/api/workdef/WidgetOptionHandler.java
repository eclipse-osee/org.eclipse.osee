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

package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class WidgetOptionHandler implements IAtsWidgetOptionHandler {

   private final Set<WidgetOption> options = new HashSet<>();

   public WidgetOptionHandler(WidgetOption... xOption) {
      for (WidgetOption item : xOption) {
         add(item);
      }
   }

   public static Collection<WidgetOption> getCollection(WidgetOption... ats) {
      Set<WidgetOption> items = new HashSet<>();
      for (WidgetOption item : ats) {
         items.add(item);
      }
      return items;
   }

   @Override
   public void add(WidgetOption widgetOption) {
      if (widgetOption.name().startsWith("ALIGN_")) {
         options.remove(WidgetOption.ALIGN_CENTER);
         options.remove(WidgetOption.ALIGN_LEFT);
         options.remove(WidgetOption.ALIGN_RIGHT);
      } else if (widgetOption == WidgetOption.FUTURE_DATE_REQUIRED) {
         options.remove(WidgetOption.NOT_FUTURE_DATE_REQUIRED);
      } else if (widgetOption == WidgetOption.NOT_FUTURE_DATE_REQUIRED) {
         options.remove(WidgetOption.FUTURE_DATE_REQUIRED);
      } else if (widgetOption == WidgetOption.HORIZONTAL_LABEL) {
         options.remove(WidgetOption.VERTICAL_LABEL);
      } else if (widgetOption == WidgetOption.EDITABLE) {
         options.remove(WidgetOption.NOT_EDITABLE);
      } else if (widgetOption == WidgetOption.NOT_EDITABLE) {
         options.remove(WidgetOption.EDITABLE);
      } else if (widgetOption == WidgetOption.NOT_REQUIRED_FOR_COMPLETION) {
         options.remove(WidgetOption.REQUIRED_FOR_COMPLETION);
      } else if (widgetOption == WidgetOption.REQUIRED_FOR_COMPLETION) {
         options.remove(WidgetOption.NOT_REQUIRED_FOR_COMPLETION);
      } else if (widgetOption == WidgetOption.NOT_REQUIRED_FOR_TRANSITION) {
         options.remove(WidgetOption.REQUIRED_FOR_TRANSITION);
      } else if (widgetOption == WidgetOption.REQUIRED_FOR_TRANSITION) {
         options.remove(WidgetOption.NOT_REQUIRED_FOR_TRANSITION);
      } else if (widgetOption == WidgetOption.AUTO_SAVE) {
         options.remove(WidgetOption.NOT_AUTO_SAVE);
      } else if (widgetOption == WidgetOption.NOT_AUTO_SAVE) {
         options.remove(WidgetOption.AUTO_SAVE);
      } else if (widgetOption == WidgetOption.NOT_ENABLED) {
         options.remove(WidgetOption.ENABLED);
      } else if (widgetOption == WidgetOption.ENABLED) {
         options.remove(WidgetOption.NOT_ENABLED);
      } else if (widgetOption == WidgetOption.FILL_NONE) {
         options.remove(WidgetOption.FILL_HORIZONTALLY);
         options.remove(WidgetOption.FILL_VERTICALLY);
      } else if (widgetOption == WidgetOption.VERTICAL_LABEL) {
         options.remove(WidgetOption.HORIZONTAL_LABEL);
      }
      options.add(widgetOption);
   }

   public void add(Collection<WidgetOption> xOption) {
      for (WidgetOption xOpt : xOption) {
         add(xOpt);
      }
   }

   @Override
   public boolean contains(WidgetOption xOption) {
      return options.contains(xOption);
   }

   @Override
   public Set<WidgetOption> getXOptions() {
      return options;
   }

   @Override
   public String toString() {
      return String.valueOf(options);
   }

   @Override
   public void remove(WidgetOption widgetOption) {
      options.remove(WidgetOption.REQUIRED_FOR_TRANSITION);
   }
}
