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
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.ats.api.config.TeamDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workdef.model.WorkDefOption;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.ats.ide.util.widgets.xx.rel.XXAbstractXXVersionWidget;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.XResultType;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXVersionSelWidget extends XXAbstractXXVersionWidget {

   public static final WidgetId ID = WidgetIdAts.XXVersionSelWidget;
   public static final String VERSION = "Version";

   private IAtsTeamDefinition teamDef;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXVersionSelWidget() {
      super(ID, VERSION);
      setOseeImage(AtsImage.VERSION);
      set(XOption.VAL_SINGLE_SEL);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<ArtifactToken> getSelectable() {
      if (teamDef != null) {
         return AtsObjects.toArtifactTokens(
            AtsApiService.get().getVersionService().getVersionsFromTeamDefHoldingVersions(teamDef));
      }
      ArtifactId teamId = getTeamId();
      if (teamId.isValid()) {
         TeamDefinition teamDef = AtsApiService.get().getTeamDefinitionService().getTeamDefinitionById(teamId);
         if (teamDef != null) {
            Collection<IAtsVersion> vers =
               AtsApiService.get().getVersionService().getVersionsFromTeamDefHoldingVersions(teamDef);
            return AtsObjects.toArtifactTokens(vers);
         }
      }
      return super.getSelectable();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected XResultData validateHandleSelection() {
      // Ok if widget has TeamWf cause versions will come from TeamWf's Team Definition
      if (xxWid.isTeamWf()) {
         return XResultData.OK_STATUS;
      }
      // If selectable was set in another way, then don't need teamDef
      else if (!getSelectable().isEmpty()) {
         return XResultData.OK_STATUS;
      }
      // Else, error cause teamDef needs to be selected/set before selecting this widget
      else if (teamDef == null && getTeamId().isInvalid()) {
         return new XResultData(XResultType.Error, "Team Definition(s) must be selected first for [%s]", getLabel());
      }
      return XResultData.OK_STATUS;
   }

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   @Override
   public XResultData isValid() {
      if (Widgets.isAccessible(labelLinkWidget) && xxWid.isTeamWf()) {
         IAtsTeamWorkflow teamWf = xxWid.getTeamWf();
         if (teamWf != null && teamWf.getWorkDefinition().hasOption(WorkDefOption.RequireTargetedVersion)) {
            if (getSelected().isEmpty()) {
               return getIsNotValidResult();
            }
         }
      }
      return super.isValid();
   }

   //////////////////////////////////////////
   // Refresh / Lifecycle
   //////////////////////////////////////////

   @Override
   public ILabelProvider getLabelProvider() {
      return new AtsObjectLabelProvider();
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

   /**
    * Convenience method for IAtsVersion since Version widget works with ArtifactToken
    */
   public void setSelectableVersions(Collection<IAtsVersion> versions) {
      super.setSelectable(AtsObjects.toArtifactTokens(versions));
   }

   @Override
   protected boolean isVersionLoaded() {
      return false; // Not Applicable to this widget
   }

   @Override
   protected void loadVersion() {
      // Not Applicable to this widget
   }

   @Override
   protected IAtsVersion getVersion() {
      return null; // Not Applicable to this widget
   }

}
