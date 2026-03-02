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
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.framework.core.widget.ISelectableValueProvider;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkWithFilteredDialogWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWfdForProgramAiWidget extends XAbstractHyperlinkWithFilteredDialogWidget<IAtsActionableItem> {

   public static final WidgetId ID = WidgetIdAts.XHyperlinkWfdForProgramAiWidget;

   ISelectableValueProvider valueProvider;

   public XHyperlinkWfdForProgramAiWidget() {
      super(ID, "");
   }

   @Override
   public Collection<IAtsActionableItem> getSelectable() {
      return Collections.castAll(valueProvider.getSelectable(this));
   }

   @Override
   public ISelectableValueProvider getValueProvider() {
      return valueProvider;
   }

   @Override
   public void setValueProvider(ISelectableValueProvider valueProvider) {
      this.valueProvider = valueProvider;
   }

}
