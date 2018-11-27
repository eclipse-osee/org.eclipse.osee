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
package org.eclipse.osee.ats.util.validate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Shawn F. Cook
 */
public class ArtifactValidationRule extends AbstractValidationRule {

   public ArtifactValidationRule(AtsApi atsApi) {
      super(atsApi);
   }

   private String getStatusMessage(Artifact itemChecked, IStatus status) {
      String link =
         XResultDataUI.getHyperlink(String.format("%s:[%s]", itemChecked.getArtifactTypeName(), itemChecked.getName()),
            AtsClientService.get().getAtsId(itemChecked), itemChecked.getBranch());
      return String.format("%s: %s", link, status.getMessage());
   }

   @Override
   public void validate(ArtifactToken artToken, XResultData results) {
      Artifact artifact = AtsClientService.get().getQueryServiceClient().getArtifact(artToken);
      Conditions.assertNotNull(artifact, "artifact not found for %s", artToken.toStringWithId());
      IStatus status = OseeValidator.getInstance().validate(IOseeValidator.LONG, artifact);

      if (!status.isOK()) {
         results.error(getStatusMessage(artifact, status));
      }
   }

   @Override
   public String getRuleDescription() {
      return "All Errors reported must be fixed.";
   }

   @Override
   public String getRuleTitle() {
      return "Artifact Validation Checks:";
   }

}
