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
import org.eclipse.osee.ats.api.program.IAtsProgram;
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
public class InsertionSearchWidget extends AbstractXComboViewerSearchWidget<IAtsInsertion> {

   public static final String INSERTION = "Insertion";
   private ProgramSearchWidget programWidget;

   public InsertionSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(INSERTION, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         Long insertionId = data.getInsertionId();
         XComboViewer combo = getWidget();
         if (insertionId != null && insertionId > 0) {
            IAtsInsertion insertion = AtsClientService.get().getProgramService().getInsertion(insertionId);
            combo.setSelected(Arrays.asList(insertion));
         }
      }
   }

   @Override
   public Collection<IAtsInsertion> getInput() {
      if (programWidget != null && programWidget.get() != null) {
         Object obj = programWidget.getWidget().getSelected();
         if (obj != null && obj instanceof IAtsProgram) {
            return Collections.castAll(AtsClientService.get().getProgramService().getInsertions(programWidget.get()));
         }
      }
      return java.util.Collections.emptyList();
   }

   public void setProgramWidget(ProgramSearchWidget programWidget) {
      this.programWidget = programWidget;
      programWidget.getWidget().getCombo().addModifyListener(new ModifyListener() {

         @Override
         public void modifyText(ModifyEvent e) {
            setup(getWidget());
         }
      });
   }

   @Override
   public String getInitialText() {
      if (programWidget == null || programWidget.get() == null) {
         return "--select program--";
      } else {
         return "";
      }
   }

}
