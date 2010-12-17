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
package org.eclipse.osee.ats.workdef;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Donald G. Dunne
 */
public class WidgetOptionHandler {

   private final Set<WidgetOption> options = new HashSet<WidgetOption>();

   public WidgetOptionHandler(WidgetOption... xOption) {
      set(xOption);
   }

   public static Collection<WidgetOption> getCollection(WidgetOption... ats) {
      Set<WidgetOption> items = new HashSet<WidgetOption>();
      for (WidgetOption item : ats) {
         items.add(item);
      }
      return items;
   }

   public void add(WidgetOption xOption) {
      if (xOption.name().startsWith("ALIGN_")) {
         options.remove(WidgetOption.ALIGN_CENTER);
         options.remove(WidgetOption.ALIGN_LEFT);
         options.remove(WidgetOption.ALIGN_RIGHT);
      } else if (xOption == WidgetOption.HORIZONTAL_LABEL) {
         options.remove(WidgetOption.VERTICAL_LABEL);
      } else if (xOption == WidgetOption.EDITABLE) {
         options.remove(WidgetOption.NOT_EDITABLE);
      } else if (xOption == WidgetOption.NOT_EDITABLE) {
         options.remove(WidgetOption.EDITABLE);
      } else if (xOption == WidgetOption.NOT_REQUIRED_FOR_COMPLETION) {
         options.remove(WidgetOption.REQUIRED_FOR_COMPLETION);
      } else if (xOption == WidgetOption.REQUIRED_FOR_COMPLETION) {
         options.remove(WidgetOption.NOT_REQUIRED_FOR_COMPLETION);
      } else if (xOption == WidgetOption.NOT_REQUIRED_FOR_TRANSITION) {
         options.remove(WidgetOption.REQUIRED_FOR_TRANSITION);
      } else if (xOption == WidgetOption.REQUIRED_FOR_TRANSITION) {
         options.remove(WidgetOption.NOT_REQUIRED_FOR_TRANSITION);
      } else if (xOption == WidgetOption.NOT_ENABLED) {
         options.remove(WidgetOption.ENABLED);
      } else if (xOption == WidgetOption.ENABLED) {
         options.remove(WidgetOption.NOT_ENABLED);
      } else if (xOption == WidgetOption.FILL_NONE) {
         options.remove(WidgetOption.FILL_HORIZONTALLY);
         options.remove(WidgetOption.FILL_VERTICALLY);
      } else if (xOption == WidgetOption.VERTICAL_LABEL) {
         options.remove(WidgetOption.HORIZONTAL_LABEL);
      }
      options.add(xOption);
   }

   public void add(WidgetOption... xOption) {
      for (WidgetOption xOpt : xOption) {
         add(xOpt);
      }
   }

   public void add(Collection<WidgetOption> xOption) {
      for (WidgetOption xOpt : xOption) {
         add(xOpt);
      }
   }

   public boolean contains(WidgetOption xOption) {
      return options.contains(xOption);
   }

   public Set<WidgetOption> getXOptions() {
      return options;
   }

   public void set(Set<WidgetOption> options) {
      this.options.clear();
      // Must go through the add method to ensure values set properly
      for (WidgetOption xOption : options) {
         add(xOption);
      }
   }

   public void set(WidgetOption options[]) {
      this.options.clear();
      // Must go through the add method to ensure values set properly
      for (WidgetOption xOption : options) {
         add(xOption);
      }
   }

   @Override
   public String toString() {
      return String.valueOf(options);
   }
}
