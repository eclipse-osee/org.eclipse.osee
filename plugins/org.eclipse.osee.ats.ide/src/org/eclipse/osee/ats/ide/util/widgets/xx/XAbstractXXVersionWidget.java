/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import java.util.Collection;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XAbstractXXWidget;

/**
 * @author Donald G. Dunne
 */
public abstract class XAbstractXXVersionWidget extends XAbstractXXWidget<ArtifactToken> {

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;
   private final RelationTypeSide relType;

   public XAbstractXXVersionWidget(WidgetId widgetId, String label, RelationTypeSide relType) {
      super(widgetId, label);
      this.relType = relType;
   }

   @Override
   public void setWidData(XWidgetData widData) {
      super.setWidData(widData);
      setSingleSelect(true);
      widData.add(XOption.CLEARABLE);
   }

   @Override
   protected ArtifactToken getSentinel() {
      return ArtifactToken.SENTINEL;
   }

   @Override
   protected void handleSelectedPersist() {
      if (xxWid.isTeamWf() && getAttributeType().isValid()) {
         IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
         IAtsChangeSet changes = AtsApiService.get().createChangeSet("Set " + getLabel());
         if (selected.isEmpty()) {
            changes.unrelateAll(teamWf, relType);
         } else {
            changes.relate(teamWf, relType, getSelectedFirst());
         }
         changes.executeIfNeeded();
      }
   }

   @Override
   public Collection<ArtifactToken> getSelected() {
      if (xxWid.isTeamWf()) {
         IAtsVersion version = xxWid.getVersion();
         if (version != null) {
            selected.clear();
            selected.add(version.getStoreObject());
         }
      }
      return super.getSelected();
   }

   @Override
   protected boolean isWidgetIcon() {
      return true;
   }

   @Override
   public Collection<ArtifactToken> getSelectable() {
      if (xxWid.isTeamWf()) {
         Collection<IAtsVersion> versions = xxWid.getSelectableVersions();
         return AtsObjects.toArtifactTokens(versions);
      }
      return super.getSelectable();
   }

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
