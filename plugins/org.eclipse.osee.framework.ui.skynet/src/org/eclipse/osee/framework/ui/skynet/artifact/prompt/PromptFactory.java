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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;

/**
 * @author Jeff C. Phillips
 */
public final class PromptFactory implements IPromptFactory {
   private final String attributeName;
   private final String displayName;
   private final Collection<? extends Artifact> artifacts;
   private final boolean persist;
   private final boolean multiLine;

   public PromptFactory(String attributeName, String displayName, Collection<? extends Artifact> artifacts, boolean persist, boolean multiLine) {
      super();
      this.attributeName = attributeName;
      this.displayName = displayName;
      this.artifacts = artifacts;
      this.persist = persist;
      this.multiLine = multiLine;
   }

   @Override
   public IHandlePromptChange createPrompt() throws OseeCoreException {
      IHandlePromptChange promptChange;

      if (AttributeTypeManager.isBaseTypeCompatible(DateAttribute.class, attributeName)) {
         promptChange = new DateHandlePromptChange(artifacts, attributeName, displayName, persist);
      } else if (AttributeTypeManager.isBaseTypeCompatible(FloatingPointAttribute.class, attributeName)) {
         promptChange =
            new StringHandlePromptChange(attributeName, persist, displayName, artifacts, NumberFormat.getInstance(),
               false);
      } else if (AttributeTypeManager.isBaseTypeCompatible(IntegerAttribute.class, attributeName)) {
         promptChange =
            new StringHandlePromptChange(attributeName, persist, displayName, artifacts,
               NumberFormat.getIntegerInstance(), false);
      } else if (AttributeTypeManager.isBaseTypeCompatible(BooleanAttribute.class, attributeName)) {
         promptChange = new BooleanHandlePromptChange(artifacts, attributeName, displayName, persist, null);
      } else if (AttributeTypeManager.isBaseTypeCompatible(EnumeratedAttribute.class, attributeName)) {
         promptChange = new EnumeratedHandlePromptChange(artifacts, attributeName, displayName, persist);
      } else if (AttributeTypeManager.isBaseTypeCompatible(StringAttribute.class, attributeName)) {
         promptChange = new StringHandlePromptChange(attributeName, persist, displayName, artifacts, null, multiLine);
      } else {
         throw new OseeStateException("Unhandled attribute type.  Can't edit through this view");
      }
      return promptChange;
   }
}
