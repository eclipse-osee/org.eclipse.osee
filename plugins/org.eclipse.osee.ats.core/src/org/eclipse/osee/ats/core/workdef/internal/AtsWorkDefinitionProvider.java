package org.eclipse.osee.ats.core.workdef.internal;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionProvider;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefGoal;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewDecision;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefReviewPeerToPeer;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefSprint;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskAtsConfig2Example;
import org.eclipse.osee.ats.core.workdef.internal.workdefs.WorkDefTaskDefault;
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
      // @formatter:off
         return Arrays.asList(
            new WorkDefGoal().build(),
            new WorkDefReviewDecision().build(),
            new WorkDefReviewPeerToPeer().build(),
            new WorkDefSprint().build(),
            new WorkDefTaskAtsConfig2Example().build(),
            new WorkDefTaskDefault().build(),
            new WorkDefTeamAtsConfig2Example().build(),
            new WorkDefTeamDefault().build(),
            new WorkDefTeamDemoCode().build(),
            new WorkDefTeamDemoReq().build(),
            new WorkDefTeamDemoSwDesign().build(),
            new WorkDefTeamDemoTest().build(),
            new WorkDefTeamSimple().build());

      // @formatter:on

   }
}
