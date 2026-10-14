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

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactState;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXStringsWidget;
import org.osgi.service.component.annotations.Component;

/**
 * Selection widget used by Action Search. Does not persist.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXBuildImpactNameWidget extends XXStringsWidget {

   public static final WidgetId ID = WidgetIdAts.XXBuildImpactNameWidget;

   private IAtsTeamDefinition teamDef;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXBuildImpactNameWidget() {
      super(ID, "Build Impact");
      setSelectable(BuildImpactState.getStateNames());
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public Collection<String> getSelectable() {
      Collection<IAtsVersion> versions =
         AtsApiService.get().getVersionService().getVersionsFromTeamDefHoldingVersions(teamDef);
      Set<String> verNames = versions.stream().map(IAtsVersion::getName).collect(Collectors.toSet());
      return verNames;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

}
