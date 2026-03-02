/*******************************************************************************
 * Copyright (c) 2023 Boeing.
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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XAbstractHyperlinkWithFilteredDialogWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkWfdForProgramsWidget extends XAbstractHyperlinkWithFilteredDialogWidget<IAtsProgram> {

   public static final WidgetId ID = WidgetIdAts.XHyperlinkWfdForProgramsWidget;

   public XHyperlinkWfdForProgramsWidget(ILabelProvider labelProvider) {
      this(ID, "Program(s)", labelProvider);
   }

   public XHyperlinkWfdForProgramsWidget(WidgetId widgetId, String label, ILabelProvider labelProvider) {
      super(ID, "Program(s)", labelProvider);
   }

   public XHyperlinkWfdForProgramsWidget() {
      this(ID, "Program(s)", null);
   }

   public XHyperlinkWfdForProgramsWidget(WidgetId widgetId, ILabelProvider labelProvider) {
      this(widgetId, "Program(s)", labelProvider);
   }

   @Override
   public Collection<IAtsProgram> getSelectable() {
      return Collections.castAll(AtsApiService.get().getConfigService().getConfigurations().getIdToProgram().values());
   }

}
