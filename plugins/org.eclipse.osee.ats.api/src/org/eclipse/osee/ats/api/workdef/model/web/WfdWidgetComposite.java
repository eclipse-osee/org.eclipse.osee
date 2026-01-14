/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.api.workdef.model.web;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Donald G. Dunne
 */
public class WfdWidgetComposite extends WfdWidgetItem {

   List<WfdWidgetItem> widgets = new ArrayList<>();
   int columns = 1;
   boolean displayName = false;

   public WfdWidgetComposite() {
      // for jax-rs
   }

   public WfdWidgetComposite(String name) {
      super(name);
   }

   public List<WfdWidgetItem> getWidgets() {
      return widgets;
   }

   public void setWidgets(List<WfdWidgetItem> widgets) {
      this.widgets = widgets;
   }

   public int getColumns() {
      return columns;
   }

   public void setColumns(int columns) {
      this.columns = columns;
   }

   public boolean isDisplayName() {
      return displayName;
   }

   public void setDisplayName(boolean displayName) {
      this.displayName = displayName;
   }

}
