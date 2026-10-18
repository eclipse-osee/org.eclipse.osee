package org.eclipse.osee.ats.core.agile.gitLab;

import org.eclipse.osee.ats.api.AtsApi;
//import org.eclipse.osee.ats.api.agile.gitLab;

public abstract class AbstractAtsGitLabService {
   // implements AtsGitLabService
   protected final AtsApi atsApi;

   public AbstractAtsGitLabService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }
}