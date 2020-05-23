/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.cpa;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsCpaService {

   String getId();

   List<CpaProgram> getPrograms();

   String getConfigJson() throws Exception;

   URI getLocation(URI uri, String id);

   CpaPcr getPcr(String pcrId);

   Map<String, CpaPcr> getPcrsByIds(Collection<String> issueIds);

   Collection<CpaBuild> getBuilds(String programId);

   /**
    * Duplicate originatingPcrId for programId,versionId and return duplicatedPcrId
    */
   String duplicate(IAtsTeamWorkflow cpaWf, String programId, String versionId, String originatingPcrId, XResultData rd);

}
