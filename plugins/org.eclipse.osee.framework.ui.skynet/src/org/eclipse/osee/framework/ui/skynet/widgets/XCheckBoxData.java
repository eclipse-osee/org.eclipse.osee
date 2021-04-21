/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets;

/**
 * @author Donald G. Dunne
 */
public class XCheckBoxData {

   private String label;
   private boolean checked = false;
   private XCheckBox checkBox;

   public XCheckBoxData(String label, boolean checked) {
      this.label = label;
      this.checked = checked;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public boolean isChecked() {
      return checked;
   }

   public void setChecked(boolean checked) {
      this.checked = checked;
   }

   public XCheckBox getCheckBox() {
      return checkBox;
   }

   public void setCheckBox(XCheckBox checkBox) {
      this.checkBox = checkBox;
   }

}
