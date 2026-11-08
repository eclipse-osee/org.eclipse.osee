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
package org.eclipse.osee.ats.rest.internal.agile.gitlab;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.gitlab.GitLabOutboundEndpoint;
import org.eclipse.osee.ats.core.agile.gitlab.AbstractAtsGitLabService;
import org.eclipse.osee.ats.rest.internal.workitem.sync.gitlab.GitLabOutboundEndpointImpl;

public class AtsGitLabServiceImpl extends AbstractAtsGitLabService {

   private static GitLabOutboundEndpoint gitLabEndpoint;

   public AtsGitLabServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }

   private GitLabOutboundEndpoint getGitLabEndpoint() {
      if (gitLabEndpoint == null) {
         gitLabEndpoint = new GitLabOutboundEndpointImpl(atsApi);
      }
      return gitLabEndpoint;
   }

   @Override
   public String createGitLabIssue(String json) {
      String createResults = getGitLabEndpoint().createGitLabIssue(json);
      return createResults;
   }
}