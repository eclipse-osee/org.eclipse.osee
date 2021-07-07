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
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;

/**
 * @author Donald G. Dunne
 */
public class XRelatedWidget extends XHyperlinkLabelValueSelection {

   XModifiedListener listener;
   String currentValue = "";

   public XRelatedWidget(String label, XModifiedListener listener) {
      super(label);
      this.listener = listener;
      setEditable(true);
   }

   @Override
   public String getCurrentValue() {
      return currentValue;
   }

   @Override
   public boolean handleSelection() {
      listener.widgetModified(this);
      return false;
   }

   public void setCurrentValue(String currentValue) {
      this.currentValue = currentValue;
   }

}
