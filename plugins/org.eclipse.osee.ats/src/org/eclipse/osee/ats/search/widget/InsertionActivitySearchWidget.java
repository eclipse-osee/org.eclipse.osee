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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;

/**
 * @author Donald G. Dunne
 */
public class InsertionActivitySearchWidget extends AbstractXComboViewerSearchWidget<IAtsInsertionActivity> {

   public static final String INSERTION_ACTIVITY = "Insertion Activity";
   private InsertionSearchWidget insertionActivityWidget;

   public InsertionActivitySearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(INSERTION_ACTIVITY, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      setup(getWidget());
      Long insertionActivityUuid = data.getInsertionActivityUuid();
      XComboViewer combo = getWidget();
      if (insertionActivityUuid != null && insertionActivityUuid > 0) {
         IAtsInsertionActivity insertionActivity =
            AtsClientService.get().getProgramService().getInsertionActivity(insertionActivityUuid);
         combo.setSelected(Arrays.asList(insertionActivity));
      }
   }

   @Override
   public Collection<IAtsInsertionActivity> getInput() {
      if (insertionActivityWidget == null || insertionActivityWidget.get() == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(
         AtsClientService.get().getProgramService().getInsertionActivities(insertionActivityWidget.get()));
   }

   public void setInsertionWidget(InsertionSearchWidget insertionWidget) {
      this.insertionActivityWidget = insertionWidget;
      insertionWidget.getWidget().addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            setup(getWidget());
         }

      });
   }

   @Override
   public String getInitialText() {
      if (insertionActivityWidget == null || insertionActivityWidget.get() == null) {
         return "--select insertion--";
      } else {
         return "";
      }
   }

}
