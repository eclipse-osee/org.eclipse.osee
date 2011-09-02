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

import java.io.File;
import java.io.IOException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public class FileToAttributeUpdateOperation extends AbstractOperation {
   private final Artifact artifact;
   private final IAttributeType attributeType;
   private final File file;
   private final AttributeModifier modifier;

   public FileToAttributeUpdateOperation(File file, Artifact artifact, IAttributeType attributeType, AttributeModifier validator) {
      super("File To Artifact Update", Activator.PLUGIN_ID);
      this.artifact = artifact;
      this.attributeType = attributeType;
      this.file = file;
      this.modifier = validator;
   }

   public FileToAttributeUpdateOperation(File file, Artifact artifact, IAttributeType attributeType) {
      this(file, artifact, attributeType, null);
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws OseeCoreException, IOException {
      String data = null;
      data = Lib.fileToString(file);

      if (modifier != null) {
         data = modifier.modifyForSave(artifact, data);
      }
      artifact.setSoleAttributeFromString(attributeType, data);
      artifact.persist(getClass().getSimpleName());
   }

}