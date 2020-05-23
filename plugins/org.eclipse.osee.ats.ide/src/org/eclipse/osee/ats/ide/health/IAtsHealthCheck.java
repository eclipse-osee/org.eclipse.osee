/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.health;

import java.util.Collection;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsHealthCheck {

   /**
    * Check artifacts for problems. Log results in resultsMap indexed by test name as key (they will be organized by key
    * in report). Insert "Error: " at beginning of resultMap value if log item is an error. This will be highlighted.
    *
    * @param results JavaTip
    */
   public void validateAtsDatabase(Collection<Artifact> artifacts, ValidateResults results);

   public Result validateChangeReports(ChangeData currentChangeData, TeamWorkFlowArtifact teamArt, XResultData resultData);

}
