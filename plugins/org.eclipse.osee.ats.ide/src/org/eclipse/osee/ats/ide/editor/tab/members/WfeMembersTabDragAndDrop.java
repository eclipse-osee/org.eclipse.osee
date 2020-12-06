/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.members;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.FilterData;
import org.eclipse.nebula.widgets.xviewer.core.model.SortingData;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldComposite;
import org.eclipse.osee.ats.ide.world.WorldViewDragAndDrop;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactTransfer;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;

/**
 * @author Donald G. Dunne
 */
public class WfeMembersTabDragAndDrop extends WorldViewDragAndDrop {

   private boolean isFeedbackAfter = false;
   private final WorldComposite worldComposite;
   private final IMemberProvider provider;

   public WfeMembersTabDragAndDrop(WorldComposite worldComposite, IMemberProvider provider, String viewId) {
      super(worldComposite, viewId);
      this.worldComposite = worldComposite;
      this.provider = provider;
   }

   private Artifact getSelectedArtifact(DropTargetEvent event) {
      if (event.item != null && event.item.getData() instanceof Artifact) {
         return AtsApiService.get().getQueryServiceIde().getArtifact(
            AtsApiService.get().getQueryServiceIde().getArtifact(event.item.getData()));
      }
      return null;
   }

   private CustomizeData getCustomizeData() {
      CustomizeData customizeData = worldComposite.getCustomizeDataCopy();
      Conditions.checkNotNull(customizeData, "Customized Data");
      return customizeData;
   }

   private FilterData getFilterData() {
      FilterData filterData = getCustomizeData().getFilterData();
      Conditions.checkNotNull(filterData, "Filter Data");
      return filterData;
   }

   private SortingData getSortingData() {
      SortingData sortingData = getCustomizeData().getSortingData();
      Conditions.checkNotNull(sortingData, "Sort Data");
      return sortingData;
   }

   private String getFilterText() {
      String filterText = getFilterData().getFilterText();
      Conditions.checkNotNull(filterText, "Filter Text");
      return filterText;
   }

   private List<String> getSortingIds() {
      return getSortingData().getSortingIds();
   }

   private boolean isSortedByCollectorsOrder() {
      List<String> sortingIds = getSortingIds();
      return sortingIds.size() == 1 && sortingIds.contains(provider.getColumnName());
   }

   private boolean isFiltered() {
      String filterText = getFilterText();
      return Strings.isValid(filterText);
   }

   private boolean isDropValid() {
      return !isFiltered() && isSortedByCollectorsOrder();
   }

   @Override
   public void operationChanged(DropTargetEvent event) {
      if (!(event.detail == 1)) {
         isFeedbackAfter = false;
      } else {
         isFeedbackAfter = true;
      }
   }

   @Override
   protected boolean isValidForArtifactDrop(DropTargetEvent event) {
      boolean validForDrop = false;
      if (ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {
         ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);

         if (artData != null) {
            Artifact[] artifacts = artData.getArtifacts();
            for (Artifact art : artifacts) {
               if (art.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact) || art.isOfType(
                  CoreArtifactTypes.UniversalGroup)) {
                  validForDrop = true;
                  break;
               }
            }
         }
      }
      return validForDrop;
   }

   @Override
   public void performDragOver(DropTargetEvent event) {
      if (isValidForArtifactDrop(event)) {
         event.detail = DND.DROP_COPY;
         Artifact selectedArtifact = getSelectedArtifact(event);
         if (selectedArtifact != null) {
            if (isFeedbackAfter) {
               event.feedback = DND.FEEDBACK_INSERT_AFTER | DND.FEEDBACK_SCROLL;
            } else {
               event.feedback = DND.FEEDBACK_INSERT_BEFORE | DND.FEEDBACK_SCROLL;
            }
         }
      } else {
         event.feedback = DND.ERROR_INVALID_DATA;
      }
   }

   @Override
   public void performDrop(final DropTargetEvent event) {
      final ArtifactData artData = ArtifactTransfer.getInstance().nativeToJava(event.currentDataType);
      final List<Artifact> droppedArtifacts = Arrays.asList(artData.getArtifacts());
      Collections.reverse(droppedArtifacts);
      final Artifact dropTarget = getSelectedArtifact(event);
      try {
         boolean dropValid = isDropValid();
         if (dropValid && ArtifactTransfer.getInstance().isSupportedType(event.currentDataType)) {

            Collections.reverse(droppedArtifacts);
            List<Artifact> members = provider.getMembers();
            Result result = provider.isAddValid(droppedArtifacts);
            if (result.isFalse()) {
               if (MessageDialog.openQuestion(Displays.getActiveShell(), "Drop Error", result.getText())) {
                  for (Artifact dropped : droppedArtifacts) {
                     dropped.deleteRelations(provider.getMemberRelationTypeSide().getOpposite());
                  }
               } else {
                  return;
               }
            }
            for (Artifact dropped : droppedArtifacts) {
               if (!dropped.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
                  continue;
               }
               if (!members.contains(dropped)) {
                  provider.addMember(dropped);
               }
               if (dropTarget != null) {
                  provider.getArtifact().setRelationOrder(provider.getMemberRelationTypeSide(), dropTarget,
                     isFeedbackAfter, dropped);
               }
            }
            provider.getArtifact().persist(WfeMembersTab.class.getSimpleName());
         } else if (!dropValid) {
            AWorkbench.popup(
               "Drag/Drop is disabled when table is filtered or sorted.\n\nSwitch to default table customization and try again.");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
      }
   }

}
