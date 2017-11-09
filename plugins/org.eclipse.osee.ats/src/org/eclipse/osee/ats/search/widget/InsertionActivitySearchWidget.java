/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.search.widget;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;

/**
 * @author Donald G. Dunne
 */
public class InsertionActivitySearchWidget extends AbstractXComboViewerSearchWidget<IAtsInsertionActivity> {

   public static final String INSERTION_ACTIVITY = "Insertion Activity";
   private InsertionSearchWidget insertionWidget;

   public InsertionActivitySearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(INSERTION_ACTIVITY, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         Long insertionActivityId = data.getInsertionActivityId();
         XComboViewer combo = getWidget();
         if (insertionActivityId != null && insertionActivityId > 0) {
            IAtsInsertionActivity insertionActivity =
               AtsClientService.get().getProgramService().getInsertionActivity(insertionActivityId);
            combo.setSelected(Arrays.asList(insertionActivity));
         }
      }
   }

   @Override
   public Collection<IAtsInsertionActivity> getInput() {
      if (insertionWidget != null && insertionWidget.get() != null) {
         Object obj = insertionWidget.getWidget().getSelected();
         if (obj != null && obj instanceof IAtsInsertion) {
            return Collections.castAll(
               AtsClientService.get().getProgramService().getInsertionActivities(insertionWidget.get()));
         }
      }
      return java.util.Collections.emptyList();
   }

   public void setInsertionWidget(InsertionSearchWidget insertionWidget) {
      this.insertionWidget = insertionWidget;
      insertionWidget.getWidget().getCombo().addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            setup(getWidget());
         }
      });

   }

   @Override
   public String getInitialText() {
      if (insertionWidget == null || insertionWidget.get() == null) {
         return "--select insertion--";
      } else {
         return "";
      }
   }

}
