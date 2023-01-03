/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.ui.skynet.templates;

import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author John Misinco
 */
public class TemplateArtifactValidator implements IOseeValidator {

   @Override
   public int getQualityOfService() {
      return SHORT;
   }

   @Override
   public boolean isApplicable(Artifact artifact, AttributeTypeToken attributeType) {
      return artifact.isOfType(CoreArtifactTypes.RendererTemplateWholeWord) && attributeType.equals(
         CoreAttributeTypes.TemplateMatchCriteria);
   }

   @Override
   public XResultData validate(Artifact artifact, AttributeTypeToken attributeType, Object proposedObject) {
      if (proposedObject instanceof String) {
         String toVerify = (String) proposedObject;
         if (Strings.isValid(toVerify)) {
            Collection<Artifact> templates =
               ArtifactQuery.getArtifactListFromTypeAndAttribute(CoreArtifactTypes.RendererTemplateWholeWord,
                  CoreAttributeTypes.TemplateMatchCriteria, toVerify, artifact.getBranch());

            if (templates.isEmpty()) {
               return XResultData.OK_STATUS;
            } else {
               String message = String.format("Invalid %s - unique constraint violation - value has already been used.",
                  attributeType);
               return XResultData.valueOf(XResultData.Type.Severe, Activator.PLUGIN_ID, message);
            }
         }
      }
      return XResultData.OK_STATUS;
   }
}
