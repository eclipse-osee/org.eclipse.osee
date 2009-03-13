/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.render.word.template;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author Andrew M. Finkbeiner
 */
public class SRSSpecialPublishingAttributeHandler implements ITemplateAttributeHandler {

   public SRSSpecialPublishingAttributeHandler() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler#process(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.render.word.template.TemplateAttribute)
    */
   @Override
   public void process(WordMLProducer wordMl, Artifact artifact, TemplateAttribute attribute) {

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler#canHandle(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.render.word.template.TemplateAttribute)
    */
   @Override
   public boolean canHandle(Artifact artifact, TemplateAttribute attribute) throws OseeCoreException {
      // This is for SRS Publishing. Do not publish unspecified attributes
      if ((attribute.getName().equals(Requirements.PARTITION) || attribute.getName().equals("Safety Criticality"))) {
         for (Attribute<?> partition : artifact.getAttributes(Requirements.PARTITION)) {
            if (partition.getValue().equals("Unspecified")) {
               return true;
            }
         }
      }
      return false;
   }

}
