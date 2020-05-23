/*********************************************************************
 * Copyright (c) 2011 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.swt.graphics.Image;

public abstract class XButtonCommon extends GenericXWidget {
   protected boolean selected = false;
   protected Image image;

   public XButtonCommon() {
      this("");
   }

   public XButtonCommon(String displayLabel) {
      super(displayLabel);
   }

   public XButtonCommon(String displayLabel, Image image) {
      this(displayLabel);
      this.image = image;
   }

   @Override
   public void refresh() {
      updateCheckWidget();
   }

   public void set(boolean selected) {
      this.selected = selected;
      updateCheckWidget();
   }

   protected void updateCheckWidget() {
      validate();
   }

   public void setImage(Image image) {
      this.image = image;
   }

   public boolean isSelected() {
      return selected;
   }

   @Override
   public Object getData() {
      return Boolean.valueOf(isSelected());
   }

   @Override
   public String toHTML(String labelFont) {
      return AHTML.getLabelStr(labelFont, getLabel() + ": ") + selected;
   }

}
