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
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsObjectLabelProvider;
import org.eclipse.osee.ats.ide.util.widgets.dialog.TeamDefinitionTreeContentProvider;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Currently only a selection widget
 *
 * @author Donald G. Dunne
 */
// CodeReview.IgnoreXxWid
@Component(service = XWidget.class, immediate = true)
public class XXTeamDefinitionsWidget extends XXAbstractBaseXWidget<IAtsTeamDefinition> {

   public static final WidgetId ID = WidgetIdAts.XXTeamDefinitionsWidget;
   public static final String TEAM_DEFINITION_S = "Team Definition(s)";

   private XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXTeamDefinitionsWidget() {
      super(ID, TEAM_DEFINITION_S);
      setAttributeType(AtsAttributeTypes.TeamDefinitionReference);
      set(XOption.WRITE, XOption.VAL_MULTI_SEL, XOption.ENABLE_ALL);
      setOseeImage(AtsImage.TEAM_DEFINITION);
      setAsProviderFor(AtsAttributeTypes.TeamDefinitionReference);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public IAtsTeamDefinition getSentinel() {
      return null;
   }

   @Override
   public Collection<IAtsTeamDefinition> getSelectable() {
      return Collections.castAll(
         AtsApiService.get().getTeamDefinitionService().getTopLevelTeamDefinitions(Active.Both));
   }

   @Override
   public boolean isOfType(Object obj) {
      return obj instanceof IAtsTeamDefinition;
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
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

   @Override
   public IContentProvider getContentProvider() {
      return new TeamDefinitionTreeContentProvider(isAll(XOption.ENABLE_ALL) ? Active.Both : Active.Active);
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

   @Override
   protected void initializeSelected() {
      if (xxWid.isArtifact()) {
         setSelected(xxWid.getTeamDef(), false);
      }
   }

}
