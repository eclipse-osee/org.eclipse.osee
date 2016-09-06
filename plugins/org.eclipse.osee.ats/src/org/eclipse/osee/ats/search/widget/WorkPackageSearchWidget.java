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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageType;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.api.insertion.IAtsInsertion;
import org.eclipse.osee.ats.api.insertion.IAtsInsertionActivity;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedIdentity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
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
public class WorkPackageSearchWidget extends AbstractXComboViewerSearchWidget<IAtsWorkPackage> {

   public static final String WORK_PACKAGE = "Work Package";
   private InsertionActivitySearchWidget insertionActivityWidget;
   private InsertionSearchWidget insertionWidget;
   private ProgramSearchWidget programWidget;
   protected static IAtsWorkPackage selectOtherActive = new NullWorkPackage("-- select from active --", "guid1");
   protected static IAtsWorkPackage selectOther = new NullWorkPackage("-- select from all --", "guid2");
   private final List<IAtsWorkPackage> selectOthers = Arrays.asList(selectOtherActive, selectOther);

   public WorkPackageSearchWidget(WorldEditorParameterSearchItem searchItem) {
      super(WORK_PACKAGE, searchItem);
   }

   @Override
   public void set(AtsSearchData data) {
      if (getWidget() != null) {
         setup(getWidget());
         Long workPackageUuid = data.getWorkPackageUuid();
         if (workPackageUuid != null && workPackageUuid > 0) {
            IAtsWorkPackage workPackage = AtsClientService.get().getProgramService().getWorkPackage(workPackageUuid);
            XComboViewer combo = getWidget();
            combo.setSelected(Arrays.asList(workPackage));
         }
      }
   }

   @Override
   public Collection<IAtsWorkPackage> getInput() {
      if (insertionActivityWidget == null || insertionActivityWidget.get() == null) {
         return selectOthers;
      }
      return Collections.castAll(
         AtsClientService.get().getEarnedValueService().getWorkPackages(insertionActivityWidget.get()));
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

   @Override
   public void setup(XWidget widget) {
      super.setup(widget);
      XComboViewer combo = getWidget();
      combo.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (selectOthers.contains(combo.getSelected())) {
               FilteredTreeDialog dialog = new FilteredTreeDialog("Select Work Package", "Select Work Package",
                  new ArrayTreeContentProvider(), new WorkPackageLabelProvider(), new StringNameSorter());
               dialog.setMultiSelect(false);
               if (combo.getSelected().equals(selectOtherActive)) {
                  Collection<IArtifactToken> tokens = ArtifactQuery.getArtifactTokenListFromTypeAndActive(
                     AtsArtifactTypes.WorkPackage, AtsUtilCore.getAtsBranch());
                  Collection<IAtsConfigObject> items = new LinkedList<>();
                  for (Artifact art : ArtifactQuery.getArtifactListFromTokens(tokens, AtsUtilCore.getAtsBranch())) {
                     items.add(AtsClientService.get().getConfigItemFactory().getWorkPackage(art));
                  }
                  dialog.setInput(items);
               } else if (combo.getSelected().equals(selectOther)) {
                  Collection<IArtifactToken> tokens = ArtifactQuery.getArtifactTokenListFromType(
                     AtsArtifactTypes.WorkPackage, AtsUtilCore.getAtsBranch());
                  Collection<IAtsConfigObject> items = new LinkedList<>();
                  for (Artifact art : ArtifactQuery.getArtifactListFromTokens(tokens, AtsUtilCore.getAtsBranch())) {
                     items.add(AtsClientService.get().getConfigItemFactory().getWorkPackage(art));
                  }
                  dialog.setInput(items);
               }
               if (dialog.open() == 0) {
                  IAtsWorkPackage selectedWorkPackage = dialog.getSelectedFirst();

                  IAtsInsertionActivity insertionActivity =
                     AtsClientService.get().getProgramService().getInsertionActivity(selectedWorkPackage);

                  IAtsInsertion insertion = AtsClientService.get().getProgramService().getInsertion(insertionActivity);

                  IAtsProgram program = AtsClientService.get().getProgramService().getProgram(insertion);

                  programWidget.getWidget().setInput(Arrays.asList(AbstractXComboViewerSearchWidget.CLEAR, program));
                  programWidget.getWidget().setSelected(Arrays.asList(program));

                  insertionWidget.getWidget().setInput(
                     Arrays.asList(AbstractXComboViewerSearchWidget.CLEAR, insertion));
                  insertionWidget.getWidget().setSelected(Arrays.asList(insertion));

                  insertionActivityWidget.getWidget().setInput(
                     Arrays.asList(AbstractXComboViewerSearchWidget.CLEAR, insertionActivity));
                  insertionActivityWidget.getWidget().setSelected(Arrays.asList(insertionActivity));

                  combo.setSelected(Arrays.asList(selectedWorkPackage));
               }
            }
         }

      });

   }

   private class WorkPackageLabelProvider extends StringLabelProvider {

      @Override
      public String getText(Object arg0) {
         if (arg0 instanceof IAtsInsertionActivity) {
            IAtsWorkPackage workPacakage = (IAtsWorkPackage) arg0;

            IAtsInsertionActivity insertionActivity =
               AtsClientService.get().getProgramService().getInsertionActivity(workPacakage);

            IAtsInsertion insertion = AtsClientService.get().getProgramService().getInsertion(insertionActivity);

            IAtsProgram program = AtsClientService.get().getProgramService().getProgram(insertion);
            return String.format("%s - %s - %s - %s", program, insertion, insertionActivity, workPacakage);
         }
         return super.getText(arg0);
      }
   }

   private static class NullWorkPackage extends NamedIdentity<String> implements IAtsWorkPackage {

      private NullWorkPackage(String name, String guid) {
         super(guid, name);
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
      public String getActivityId() throws OseeCoreException {
         return null;
      }

      @Override
      public String getActivityName() throws OseeCoreException {
         return null;
      }

      @Override
      public String getWorkPackageId() throws OseeCoreException {
         return null;
      }

      @Override
      public String getWorkPackageProgram() throws OseeCoreException {
         return null;
      }

      @Override
      public AtsWorkPackageType getWorkPackageType() throws OseeCoreException {
         return null;
      }

      @Override
      public int getWorkPackagePercent() throws OseeCoreException {
         return 0;
      }

      @Override
      public Date getStartDate() throws OseeCoreException {
         return null;
      }

      @Override
      public Date getEndDate() throws OseeCoreException {
         return null;
      }

      @Override
      public Long getUuid() {
         return null;
      }

   }

   public void setInsertionWidget(InsertionSearchWidget insertionWidget) {
      this.insertionWidget = insertionWidget;
   }

   public void setProgramWidget(ProgramSearchWidget programWidget) {
      this.programWidget = programWidget;
   }

}
