/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.core.config.ActionableItemSorter;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewerWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XActionableItemComboWidget extends XComboViewerWidget {

   public static WidgetId ID = WidgetIdAts.XActionableItemComboWidget;

   private IAtsActionableItem selectedAi = null;
   private final Active active;

   public XActionableItemComboWidget() {
      this(Active.Active);
   }

   public XActionableItemComboWidget(Active active) {
      this(ID, active);
   }

   public XActionableItemComboWidget(WidgetId widgetId, Active active) {
      super(widgetId, "Actionable Item", SWT.READ_ONLY);
      this.active = active;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      Collection<IAtsActionableItem> ais = null;
      try {
         ais = AtsApiService.get().getActionableItemService().getActionableItems(active,
            AtsApiService.get().getQueryService());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, "Error loading actionable items", ex);
      }

      if (ais != null) {
         List<IAtsActionableItem> sortedAiArts = new ArrayList<>();
         sortedAiArts.addAll(ais);
         Collections.sort(sortedAiArts, new ActionableItemSorter());
         getComboViewer().setInput(sortedAiArts);
         ArrayList<Object> defaultSelection = new ArrayList<>();
         defaultSelection.add("--select--");
         setSelected(defaultSelection);
         addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               selectedAi = (IAtsActionableItem) getSelected();
            }
         });
      }
   }

   public IAtsActionableItem getSelectedAi() {
      return selectedAi;
   }

}
