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
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
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
public class WorkPackageSearchWidget extends AbstractXComboViewerSearchWidget<IAtsWorkPackage> {

   public static final String WORK_PACKAGE = "Work Package";
   private InsertionActivitySearchWidget insertionActivityWidget;

   public WorkPackageSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(WORK_PACKAGE, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         Long workPackageId = data.getWorkPackageId();
         if (workPackageId != null && workPackageId > 0) {
            IAtsWorkPackage workPackage = AtsClientService.get().getProgramService().getWorkPackage(workPackageId);
            XComboViewer combo = getWidget();
            combo.setSelected(Arrays.asList(workPackage));
         }
      }
   }

   @Override
   public Collection<IAtsWorkPackage> getInput() {
      if (insertionActivityWidget != null && insertionActivityWidget.get() != null) {
         Object selected = insertionActivityWidget.getWidget().getSelected();
         if (selected != null && selected instanceof IAtsInsertionActivity) {
            return Collections.castAll(
               AtsClientService.get().getEarnedValueService().getWorkPackages(insertionActivityWidget.get()));
         }
      }
      return java.util.Collections.emptyList();
   }

   public void setInsertionActivityWidget(InsertionActivitySearchWidget insertionActivityWidget) {
      this.insertionActivityWidget = insertionActivityWidget;
      insertionActivityWidget.getWidget().getCombo().addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            setup(getWidget());
         }
      });
   }

   @Override
   public String getInitialText() {
      if (insertionActivityWidget == null || insertionActivityWidget.get() == null) {
         return "--select activity--";
      } else {
         return "";
      }
   }

}
