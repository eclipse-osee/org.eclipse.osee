/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public class FileToAttributeUpdateOperation extends AbstractOperation {
   private final Artifact artifact;
   private final AttributeTypeGeneric<?> attributeType;
   private final File file;
   private final AttributeModifier modifier;

   public FileToAttributeUpdateOperation(File file, Artifact artifact, AttributeTypeGeneric<?> attributeType, AttributeModifier validator) {
      super("File To Artifact Update", Activator.PLUGIN_ID);
      this.artifact = artifact;
      this.attributeType = attributeType;
      this.file = file;
      this.modifier = validator;
   }

   public FileToAttributeUpdateOperation(File file, Artifact artifact, AttributeTypeGeneric<?> attributeType) {
      this(file, artifact, attributeType, null);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws IOException {
      InputStream stream;

      if (modifier != null) {
         stream = modifier.modifyForSave(artifact, file);
      } else {
         stream = new BufferedInputStream(new FileInputStream(file));
      }

      try {
         artifact.setSoleAttributeFromStream(attributeType, stream);
         artifact.persist(getClass().getSimpleName());
      } finally {
         Lib.close(stream);
      }
   }
}
