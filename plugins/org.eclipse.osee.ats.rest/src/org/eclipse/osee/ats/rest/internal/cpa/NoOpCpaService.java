/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.cpa;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.ats.api.cpa.CpaBuild;
import org.eclipse.osee.ats.api.cpa.CpaPcr;
import org.eclipse.osee.ats.api.cpa.CpaProgram;
import org.eclipse.osee.ats.api.cpa.IAtsCpaService;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.framework.core.util.result.XResultData;

/**
 * @author Roberto E. Escobar
 */
public class NoOpCpaService implements IAtsCpaService {

   private static final String CPA_SERVER_ID = "no-op-service";

   @Override
   public String getId() {
      return CPA_SERVER_ID;
   }

   @Override
   public List<CpaProgram> getPrograms() {
      return Collections.emptyList();
   }

   @Override
   public String getConfigJson() throws Exception {
      return "{}";
   }

   @Override
   public URI getLocation(URI uri, String id) {
      return null;
   }

   @Override
   public CpaPcr getPcr(String pcrId) {
      return null;
   }

   @Override
   public Map<String, CpaPcr> getPcrsByIds(Collection<String> issueIds) {
      return Collections.emptyMap();
   }

   @Override
   public Collection<CpaBuild> getBuilds(String programId) {
      return Collections.emptyList();
   }

   @Override
   public String duplicate(IAtsTeamWorkflow cpaWf, String programId, String versionId, String originatingPcrId, XResultData rd) {
      return null;
   }

}
