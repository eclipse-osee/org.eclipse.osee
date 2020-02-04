/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.util.widgets;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.Version;
import org.eclipse.osee.ats.ide.column.FoundInVersionColumnUI;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;

/**
 * @author Jeremy A. Midvidy
 */
public class XFoundInVersionWidget extends XHyperlabelVersionSelection implements IArtifactWidget {

   public static final String WIDGET_ID = XFoundInVersionWidget.class.getSimpleName();
   public static RelationTypeSide FOUND_VERSION_RELATION = AtsRelationTypes.TeamWorkflowToFoundInVersion_Version;
   public Collection<Version> selectedVersions = new HashSet<>();
   private Artifact artifact;

   public XFoundInVersionWidget(String label) {
      super(label);
   }

   public XFoundInVersionWidget() {
      this("Found In Version");
   }

   private RelationTypeSide getRelation() {
      return FOUND_VERSION_RELATION;
   }

   @Override
   public String getCurrentValue() {
      selectedVersions = (Collections.castAll(getArtifact().getRelatedArtifacts(getRelation())));
      return Collections.toString(",", selectedVersions);
   }

   @Override
   public boolean handleClear() {
      selectedVersions.clear();
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("Update Found-In-Version");
      changes.unrelateAll(getArtifact(), getRelation());
      changes.executeIfNeeded();
      notifyXModifiedListeners();
      return true;
   }

   @SuppressWarnings("cast")
   @Override
   public boolean handleSelection() {
      try {
         if (FoundInVersionColumnUI.getInstance().promptChangeVersion((TeamWorkFlowArtifact) getArtifact())) {
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
