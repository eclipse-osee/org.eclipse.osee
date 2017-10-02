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
package org.eclipse.osee.framework.ui.skynet.artifact.prompt;

import java.text.NumberFormat;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.LongAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;

/**
 * @author Jeff C. Phillips
 */
public final class PromptFactory implements IPromptFactory {

   @Override
   public IHandlePromptChange createPrompt(AttributeTypeId attributeType, String displayName, Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine)  {
      IHandlePromptChange promptChange;

      if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeType)) {
         promptChange = new DateHandlePromptChange(artifacts, attributeType, displayName, persist);
      } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeType)) {
         promptChange = new StringHandlePromptChange(attributeType, persist, displayName, artifacts,
            NumberFormat.getInstance(), false);
      } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeType)) {
         promptChange = new StringHandlePromptChange(attributeType, persist, displayName, artifacts,
            NumberFormat.getIntegerInstance(), false);
      } else if (AttributeTypeManager.isBaseTypeCompatible(LongAttribute.class, attributeType)) {
         promptChange = new StringHandlePromptChange(attributeType, persist, displayName, artifacts,
            NumberFormat.getNumberInstance(), false);
      } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeType)) {
         promptChange = new BooleanHandlePromptChange(artifacts, attributeType, displayName, persist, null);
      } else if (AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeType)) {
         promptChange = new EnumeratedHandlePromptChange(artifacts, attributeType, displayName, persist);
      } else if (AttributeTypeManager.isBaseTypeCompatible(StringAttribute.class, attributeType)) {
         promptChange = new StringHandlePromptChange(attributeType, persist, displayName, artifacts, null, multiLine);
      } else {
         throw new OseeStateException("Unhandled attribute type.  Can't edit through this view");
      }
      return promptChange;
   }
}
