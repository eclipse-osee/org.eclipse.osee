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
package org.eclipse.osee.framework.ui.skynet.templates;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
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
   public boolean isApplicable(Artifact artifact, AttributeTypeId attributeType) {
      return artifact.isOfType(CoreArtifactTypes.RendererTemplate) && attributeType.equals(
         CoreAttributeTypes.TemplateMatchCriteria);
   }

   @Override
   public IStatus validate(Artifact artifact, AttributeTypeToken attributeType, Object proposedObject) {
      if (proposedObject instanceof String) {
         String toVerify = (String) proposedObject;
         if (Strings.isValid(toVerify)) {
            Collection<Artifact> templates =
               ArtifactQuery.getArtifactListFromTypeAndAttribute(CoreArtifactTypes.RendererTemplate,
                  CoreAttributeTypes.TemplateMatchCriteria, toVerify, artifact.getBranch());

            if (templates.isEmpty()) {
               return Status.OK_STATUS;
            } else {
               String message = String.format("Invalid %s - unique constraint violation - value has already been used.",
                  attributeType);
               return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
            }
         }
      }
      return Status.OK_STATUS;
   }
}
