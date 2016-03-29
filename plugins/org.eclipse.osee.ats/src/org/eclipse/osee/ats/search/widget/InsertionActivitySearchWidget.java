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
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
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
public class InsertionActivitySearchWidget extends AbstractXComboViewerSearchWidget<IAtsInsertionActivity> {

   public static final String INSERTION_ACTIVITY = "Insertion Activity";
   private InsertionSearchWidget insertionWidget;
   private ProgramSearchWidget programWidget;
   protected static IAtsInsertionActivity selectOtherActive =
      new NullInsertionActivity("-- select from active --", 1L);
   protected static IAtsInsertionActivity selectOther = new NullInsertionActivity("-- select from all --", 2L);
   private final List<IAtsInsertionActivity> selectOthers =
      Arrays.asList(selectOtherActive, selectOther);

   public InsertionActivitySearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(INSERTION_ACTIVITY, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         Long insertionActivityUuid = data.getInsertionActivityUuid();
         XComboViewer combo = getWidget();
         if (insertionActivityUuid != null && insertionActivityUuid > 0) {
            IAtsInsertionActivity insertionActivity =
               AtsClientService.get().getProgramService().getInsertionActivity(insertionActivityUuid);
            combo.setSelected(Arrays.asList(insertionActivity));
         }
      }
   }

   @Override
   public Collection<IAtsInsertionActivity> getInput() {
      if (insertionWidget == null || insertionWidget.get() == null) {
         return selectOthers;
      }
      return Collections.castAll(
         AtsClientService.get().getProgramService().getInsertionActivities(insertionWidget.get()));
   }

   public void setProgramWidget(ProgramSearchWidget programWidget) {
      this.programWidget = programWidget;
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

   @Override
   public void setup(XWidget widget) {
      super.setup(widget);
      XComboViewer combo = getWidget();
      combo.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (selectOthers.contains(combo.getSelected())) {
               FilteredTreeDialog dialog =
                  new FilteredTreeDialog("Select Insertion Activity", "Select Insertion Activity",
                     new ArrayTreeContentProvider(), new InsertionActivityLabelProvider(), new StringNameSorter());
               dialog.setMultiSelect(false);
               if (combo.getSelected().equals(selectOtherActive)) {
                  dialog.setInput(
                     AtsClientService.get().getQueryService().createQuery(AtsArtifactTypes.InsertionActivity).andActive(
                        true).getItems());
               } else if (combo.getSelected().equals(selectOther)) {
                  dialog.setInput(AtsClientService.get().getQueryService().createQuery(
                     AtsArtifactTypes.InsertionActivity).getItems());
               }
               if (dialog.open() == 0) {
                  IAtsInsertionActivity selectedInsertionActivity = dialog.getSelectedFirst();

                  IAtsInsertion insertion =
                     AtsClientService.get().getProgramService().getInsertion(selectedInsertionActivity);

                  IAtsProgram program = AtsClientService.get().getProgramService().getProgram(insertion);

                  programWidget.getWidget().setInput(Arrays.asList(AbstractXComboViewerSearchWidget.CLEAR, program));
                  programWidget.getWidget().setSelected(Arrays.asList(program));

                  insertionWidget.getWidget().setInput(
                     Arrays.asList(AbstractXComboViewerSearchWidget.CLEAR, insertion));
                  insertionWidget.getWidget().setSelected(Arrays.asList(insertion));

                  combo.setSelected(Arrays.asList(selectedInsertionActivity));
               }
            }
         }

      });

   }

   private class InsertionActivityLabelProvider extends StringLabelProvider {

      @Override
      public String getText(Object arg0) {
         if (arg0 instanceof IAtsInsertionActivity) {
            IAtsInsertionActivity insertionActivity = (IAtsInsertionActivity) arg0;
            IAtsInsertion insertion = AtsClientService.get().getProgramService().getInsertion(insertionActivity);
            IAtsProgram program = AtsClientService.get().getProgramService().getProgram(insertion);
            return String.format("%s - %s - %s", program, insertion, insertionActivity);
         }
         return super.getText(arg0);
      }
   }

   private static class NullInsertionActivity extends NamedIdentity<Long> implements IAtsInsertionActivity {

      private NullInsertionActivity(String name, Long uuid) {
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
      public int compareTo(Named o) {
         return 0;
      }

      @Override
      public Long getUuid() {
         return super.getGuid();
      }

      @Override
      public long getInsertionUuid() {
         return 0;
      }
   }

}
