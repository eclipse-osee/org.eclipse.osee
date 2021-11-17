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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;

/**
 * @author Donald G. Dunne
 */
public class XHyperlinkLabelValueStringSel extends XHyperlinkLabelValueSelection {

   String value = "";

   public XHyperlinkLabelValueStringSel() {
      this("");
   }

   public XHyperlinkLabelValueStringSel(String label) {
      super(label);
   }

   @Override
   public boolean handleSelection() {
      EntryDialog dialog = new EntryDialog(getLabel(), "Enter " + getLabel());
      if (dialog.open() == Window.OK) {
         value = dialog.getEntry();
         refresh();
      }
      return false;
   }

   @Override
   public String getCurrentValue() {
      return Strings.isValid(value) ? value : "Not Set";
   }

   public String getValue() {
      return value;
   }

   public void setValue(String value) {
      this.value = value;
      refresh();
   }

   @Override
   public boolean isEmpty() {
      return Strings.isInValid(value) || "Not Set".equals(value);
   }

   @Override
   public IStatus isValid() {
      if (isRequiredEntry() && isEmpty()) {
         return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getLabel() + " must be selected.");
      }
      return Status.OK_STATUS;
   }

}
