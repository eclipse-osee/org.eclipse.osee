package org.eclipse.osee.ats.core.workdef.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefGoal;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewDecision;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerDemoSwDesign;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerToPeer;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefSprint;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDefault;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDemoSwDesign;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDefault;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoCode;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoReq;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoSwDesign;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamDemoTest;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTeamSimple;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProvider implements IAtsWorkDefinitionProvider {

   @Override
   public Collection<IAtsWorkDefinition> getWorkDefinitions() {
      // return empty if not AtsDB
      List<IAtsWorkDefinition> ret = new ArrayList<IAtsWorkDefinition>();
      // @formatter:off
         ret.addAll(Arrays.asList(
            new WorkDefGoal().build(),
            new WorkDefReviewDecision().build(),
            new WorkDefReviewPeerToPeer().build(),
            new WorkDefSprint().build(),
            new WorkDefTaskAtsConfig2Example().build(),
            new WorkDefTaskDefault().build(),
            new WorkDefTeamAtsConfig2Example().build(),
            new WorkDefTeamDefault().build(),
            new WorkDefTeamSimple().build()));
         if (isDemoDb()) {
            ret.addAll(Arrays.asList(
            new WorkDefTeamDemoCode().build(),
            new WorkDefTeamDemoReq().build(),
            new WorkDefTeamDemoSwDesign().build(),
            new WorkDefTeamDemoTest().build(),
            new WorkDefTaskDemoSwDesign().build(),
            new WorkDefReviewPeerDemoSwDesign().build()));
      }
      // @formatter:on
      return ret;
   }

   public boolean isDemoDb() {
      return AtsApiService.get().getUserService().getUserById("3333") != null;
   }

}
