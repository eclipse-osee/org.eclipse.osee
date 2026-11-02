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
package org.eclipse.osee.ats.ide.search.widget.xx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Selection widget used by Action Search. Does not persist.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXPrBuildSelWidget extends XXAbstractBaseXWidget<ArtifactToken> {

   public static final WidgetId ID = WidgetIdAts.XXPrBuildSelWidget;
   public static final String LABEL = "Previous PRs";
   private static final String OPEN_PRS = "Open PR Lists";

   private IAtsTeamDefinition teamDef;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXPrBuildSelWidget() {
      super(ID, LABEL);
      set(XOption.VAL_SINGLE_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public ArtifactToken getSentinel() {
      return ArtifactToken.SENTINEL;
   }

   @Override
   public Collection<ArtifactToken> getSelectable() {
      if (teamDef == null) {
         return Collections.emptyList();
      }
      Artifact teamDefArt = (Artifact) AtsApiService.get().getQueryService().getArtifact(teamDef.getId());
      if (teamDefArt == null) {
         return Collections.emptyList();
      }
      Artifact openPrsFolder = null;
      for (Artifact child : teamDefArt.getChildren()) {
         if (child.getName().contains(OPEN_PRS)) {
            openPrsFolder = child;
            break;
         }
      }
      if (openPrsFolder == null) {
         return Collections.emptyList();
      }
      List<ArtifactToken> items = new ArrayList<>();
      for (Artifact child : openPrsFolder.getChildren()) {
         if (child.isOfType(CoreArtifactTypes.EnumeratedArtifact)) {
            items.add(child);
         }
      }
      return items;
   }

   @Override
   protected boolean handleSelection() {
      if (teamDef == null) {
         AWorkbench.popup("Team Definition must be selected");
         return false;
      }
      Collection<ArtifactToken> selectable = getSelectable();
      if (selectable.isEmpty()) {
         AWorkbench.popup("No PR lists found under team definition");
         return false;
      }
      return super.handleSelection();
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public IAtsTeamDefinition getTeamDef() {
      return teamDef;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

}
