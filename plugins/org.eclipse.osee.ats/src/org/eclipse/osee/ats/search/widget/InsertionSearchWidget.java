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
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;

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
      setup(getWidget());
      Long insertionUuid = data.getInsertionUuid();
      XComboViewer combo = getWidget();
      if (insertionUuid != null && insertionUuid > 0) {
         IAtsInsertion insertion = AtsClientService.get().getProgramService().getInsertion(insertionUuid);
         combo.setSelected(Arrays.asList(insertion));
      }
   }

   @Override
   public Collection<IAtsInsertion> getInput() {
      if (programWidget == null || programWidget.get() == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(AtsClientService.get().getProgramService().getInsertions(programWidget.get()));
   }

   public void setProgramWidget(ProgramSearchWidget programWidget) {
      this.programWidget = programWidget;
      programWidget.getWidget().addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
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
