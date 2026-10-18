package org.eclipse.osee.ats.rest.internal.agile.gitLab;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.gitLab.GitLabOutboundEndpoint;
import org.eclipse.osee.ats.core.agile.gitLab.AbstractAtsGitLabService;

public class AtsGitLabServiceImpl extends AbstractAtsGitLabService {

   private static GitLabOutboundEndpoint gitLabEndpoint;

   public AtsGitLabServiceImpl(AtsApi atsApi) {
      super(atsApi);
   }
}