/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.rule.validation;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultBrowserHyperCmd;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractValidationRule {

   protected final AtsApi atsApi;

   public AbstractValidationRule(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   /**
    * @param artifact to validate as a collection
    */
   public void validateAll(Collection<ArtifactToken> artifacts, XResultData results) {
      // do nothing
   }

   /**
    * @param artifact to validate individually
    */
   public abstract void validate(ArtifactToken artifact, XResultData results);

   public abstract String getRuleDescription();

   public abstract String getRuleTitle();

   public void logError(ArtifactToken artifact, String message, XResultData results) {
      String errStr = getHyperlink(artifact, message);
      results.errorf(errStr);
   }

   /**
    * @return message with Artifact Editor hyperlink in form "<artifact type name> [art name][art id] <message> open"
    */
   public String getHyperlink(ArtifactToken artifact, String message) {
      String artTypeName = artifact.getArtifactType().getName();
      String link = getHyperlink("open", artifact.getIdString(), artifact.getBranch());
      return String.format("%s %s %s %s", artTypeName, artifact.toStringWithId(), message, link);
   }

   public String getHyperlink(String name, ArtifactId artifact, BranchId branch) {
      return getHyperlink(name, artifact.getIdString(), branch);
   }

   public String getHyperlink(String name, String id, BranchId branch) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openArtifactBranch,
         id + "(" + branch.getId() + ")"), name);
   }

}
