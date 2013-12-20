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
package org.eclipse.osee.display.view.web;

import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class OseeFooter extends HorizontalLayout {

   private boolean populated;

   public OseeFooter() {
   }

   @Override
   public void attach() {
      if (populated) {
         // Only populate the layout once
         return;
      }

      Label summary = new Label(getApplicationInfo());
      addComponent(summary);
      populated = true;

      this.setStyleName(CssConstants.OSEE_FOOTER_BAR);
      this.setWidth(100, UNITS_PERCENTAGE);
      this.setHeight(null);
   }

   public String getApplicationInfo() {
      StringBuilder builder = new StringBuilder();
      builder.append("Version: ");
      builder.append(getApplication().getVersion());
      return builder.toString();
   }

}