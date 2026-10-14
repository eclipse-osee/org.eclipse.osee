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
import java.util.List;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.skynet.core.utility.Branches;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXAbstractBaseXWidget;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.osgi.service.component.annotations.Component;

/**
 * Selection widget used by Action Search. Does not persist.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXPleConfigurationWidget extends XXAbstractBaseXWidget<BranchViewToken> {

   public static final WidgetId ID = WidgetIdAts.XXPleConfigurationWidget;
   public static final String LABEL = "Configuration";

   protected IAtsTeamDefinition teamDef;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXPleConfigurationWidget() {
      super(ID, LABEL);
      set(XOption.VAL_SINGLE_SEL, XOption.CLEAR);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public BranchViewToken getSentinel() {
      return BranchViewToken.SENTINEL;
   }

   @Override
   public Collection<BranchViewToken> getSelectable() {
      if (teamDef != null) {
         return getSelectableBranchViewTokens(teamDef);
      }
      return super.getSelectable();
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

   public static List<BranchViewToken> getSelectableBranchViewTokens(IAtsTeamDefinition teamDef) {
      List<BranchViewToken> tokens = new ArrayList<>();
      if (teamDef != null) {
         IAtsProgram program = AtsApiService.get().getProgramService().getProgram(teamDef);
         if (program != null) {
            BranchId branch = AtsApiService.get().getProgramService().getProductLineBranch(program);
            if (Branches.isValid(branch)) {
               ApplicabilityEndpoint applicEndpoint =
                  AtsApiService.get().getOseeClient().getApplicabilityEndpoint(branch);
               tokens.addAll(applicEndpoint.getBranchViewTokens());
               return tokens;
            }
         }
      }
      return tokens;
   }

   public void setTeamDef(IAtsTeamDefinition teamDef) {
      this.teamDef = teamDef;
   }

}
