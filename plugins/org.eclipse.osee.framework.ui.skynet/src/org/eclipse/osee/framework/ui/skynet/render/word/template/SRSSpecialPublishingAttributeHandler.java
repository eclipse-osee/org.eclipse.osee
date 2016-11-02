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

import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author Andrew M. Finkbeiner
 */
public class SRSSpecialPublishingAttributeHandler implements ITemplateAttributeHandler {

   @Override
   public void process(WordMLProducer wordMl, Artifact artifact, TemplateAttribute attribute) {
      // do nothing
   }

   @Override
   public boolean canHandle(Artifact artifact, TemplateAttribute attribute) throws OseeCoreException {
      // This is for SRS Publishing. Do not publish unspecified attributes
      if (attribute.getName().equals(CoreAttributeTypes.Partition.getName()) || attribute.getName().equals(
         CoreAttributeTypes.SeverityCategory.getName())) {
         for (Attribute<?> partition : artifact.getAttributes(CoreAttributeTypes.Partition)) {
            if (partition.getValue().equals(AttributeId.UNSPECIFIED)) {
               return true;
            }
         }
      }
      return false;
   }
}