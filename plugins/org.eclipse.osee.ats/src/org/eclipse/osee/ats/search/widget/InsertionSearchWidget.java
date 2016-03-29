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
import java.util.List;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.plugin.util.ArrayTreeContentProvider;
import org.eclipse.osee.framework.ui.plugin.util.StringLabelProvider;
import org.eclipse.osee.framework.ui.skynet.util.StringNameSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeDialog;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author Donald G. Dunne
 */
public class InsertionSearchWidget extends AbstractXComboViewerSearchWidget<IAtsInsertion> {

   public static final String INSERTION = "Insertion";
   private ProgramSearchWidget programWidget;
   protected static IAtsInsertion selectOtherActive = new NullInsertion("-- select from active --", 1L);
   protected static IAtsInsertion selectOther = new NullInsertion("-- select from all --", 2L);
   private final List<IAtsInsertion> selectOthers = Arrays.asList(selectOtherActive, selectOther);

   public InsertionSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(INSERTION, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         Long insertionUuid = data.getInsertionUuid();
         XComboViewer combo = getWidget();
         if (insertionUuid != null && insertionUuid > 0) {
            IAtsInsertion insertion = AtsClientService.get().getProgramService().getInsertion(insertionUuid);
            combo.setSelected(Arrays.asList(insertion));
         }
      }
   }

   @Override
   public Collection<IAtsInsertion> getInput() {
      if (programWidget == null || programWidget.get() == null) {
         return selectOthers;
      }
      return Collections.castAll(AtsClientService.get().getProgramService().getInsertions(programWidget.get()));
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

   private static class NullInsertion extends NamedIdentity<Long> implements IAtsInsertion {

      private NullInsertion(String name, Long uuid) {
         super(uuid, name);
      }

      @Override
      public boolean isActive() {
         return true;
      }

      @Override
      public String toStringWithId() {
         return String.format("%s - %s", getName(), getUuid());
      }

      @Override
      public ArtifactId getStoreObject() {
         return null;
      }

      @Override
      public void setStoreObject(ArtifactId artifact) {
         // do nothing
      }

      @Override
      public String getDescription() {
         return null;
      }

      @Override
      public long getProgramUuid() {
         return 0;
      }

      @Override
      public int compareTo(Named o) {
         return 0;
      }

      @Override
      public Long getUuid() {
         return super.getGuid();
      }
   }

   @Override
   public void setup(XWidget widget) {
      super.setup(widget);
      XComboViewer combo = getWidget();
      combo.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (selectOthers.contains(combo.getSelected())) {
               FilteredTreeDialog dialog = new FilteredTreeDialog("Select Insertion", "Select Insertion",
                  new ArrayTreeContentProvider(), new InsertionLabelProvider(), new StringNameSorter());
               dialog.setMultiSelect(false);
               if (combo.getSelected().equals(selectOtherActive)) {
                  dialog.setInput(
                     AtsClientService.get().getQueryService().createQuery(AtsArtifactTypes.Insertion).andActive(
                        true).getItems());
               } else if (combo.getSelected().equals(selectOther)) {
                  dialog.setInput(
                     AtsClientService.get().getQueryService().createQuery(AtsArtifactTypes.Insertion).getItems());
               }
               if (dialog.open() == 0) {
                  IAtsInsertion selectedInsertion = dialog.getSelectedFirst();
                  IAtsProgram program = AtsClientService.get().getProgramService().getProgram(selectedInsertion);
                  List<Object> programs = Arrays.asList(AbstractXComboViewerSearchWidget.CLEAR, program);
                  programWidget.getWidget().setInput(programs);
                  programWidget.getWidget().setSelected(programs);

                  combo.setSelected(Arrays.asList(selectedInsertion));
               }
            }
         }

      });

   }

   private class InsertionLabelProvider extends StringLabelProvider {

      @Override
      public String getText(Object arg0) {
         if (arg0 instanceof IAtsInsertion) {
            IAtsInsertion insertion = (IAtsInsertion) arg0;
            IAtsProgram program = AtsClientService.get().getProgramService().getProgram(insertion);
            return String.format("%s - %s", program, insertion);
         }
         return super.getText(arg0);
      }
   }
}
