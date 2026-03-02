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

import org.eclipse.osee.framework.core.widget.WidgetId;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWfdForEnumAttrArtWidget extends XHyperlinkWfdForEnumAttrWidget {

   public static final WidgetId ID = WidgetId.XHyperlinkWfdForEnumAttrArtWidget;

   public XHyperlinkWfdForEnumAttrArtWidget() {
      super(ID);
   }

   @Override
   protected void handleSelectionPersist(String selected) {
      getArtifact().setSoleAttributeValue(getAttributeType(), selected);
      getArtifact().persistInThread("Set Value");
   }

   @Override
   public String getCurrentValue() {
      return getArtifact().getSoleAttributeValue(getAttributeType(), "");
   }

}
