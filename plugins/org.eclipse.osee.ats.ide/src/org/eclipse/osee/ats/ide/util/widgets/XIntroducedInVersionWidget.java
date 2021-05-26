/*********************************************************************
 * Copyright (c) 2021 Boeing
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

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.ide.column.IntroducedInVersionColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactWidget;

/**
 * @author Donald G. Dunne
 */
public class XIntroducedInVersionWidget extends XHyperlabelVersionSelection implements ArtifactWidget {

   public static final String WIDGET_ID = XIntroducedInVersionWidget.class.getSimpleName();
   public static RelationTypeSide INTRODUCED_VERSION_RELATION =
      AtsRelationTypes.TeamWorkflowToIntroducedInVersion_Version;
   public Collection<Version> selectedVersions = new HashSet<>();
   private Artifact artifact;

   public XIntroducedInVersionWidget(String label) {
      super(label);
   }

   public XIntroducedInVersionWidget() {
      this("Introduced In Version");
   }

   private RelationTypeSide getRelation() {
      return INTRODUCED_VERSION_RELATION;
   }

   @Override
   public String getCurrentValue() {
      selectedVersions = (Collections.castAll(getArtifact().getRelatedArtifacts(getRelation())));
      return Collections.toString(",", selectedVersions);
   }

   @Override
   public boolean handleClear() {
      selectedVersions.clear();
      IAtsChangeSet changes = AtsApiService.get().createChangeSet("Update Introduced-In-Version");
      changes.unrelateAll(getArtifact(), getRelation());
      changes.executeIfNeeded();
      notifyXModifiedListeners();
      return true;
   }

   @Override
   public boolean handleSelection() {
      try {
         if (IntroducedInVersionColumnUI.getInstance().promptChangeVersion((TeamWorkFlowArtifact) getArtifact())) {
            notifyXModifiedListeners();
            refresh();
            return true;
         }
         return false;
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return false;
   }

   @Override
   public boolean isEmpty() {
      return selectedVersions.isEmpty();
   }

   @Override
   public void setArtifact(Artifact art) {
      if (art instanceof TeamWorkFlowArtifact) {
         this.artifact = art;
      }
   }

   @Override
   public Artifact getArtifact() {
      return this.artifact;
   }

   @Override
   public void revert() {
      //
   }

   @Override
   public void saveToArtifact() {
      //
   }

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

}
