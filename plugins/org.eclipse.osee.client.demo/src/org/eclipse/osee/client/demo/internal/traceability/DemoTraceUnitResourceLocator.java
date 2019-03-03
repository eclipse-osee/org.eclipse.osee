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
package org.eclipse.osee.client.demo.internal.traceability;

import java.nio.CharBuffer;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.osee.define.ide.traceability.ITraceUnitResourceLocator;
import org.eclipse.osee.define.ide.traceability.ResourceIdentifier;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author John R. Misinco
 */
public class DemoTraceUnitResourceLocator implements ITraceUnitResourceLocator {

   @Override
   public boolean isValidDirectory(IFileStore fileStore) {
      boolean isValid = false;
      String name = fileStore.getName();
      if (Strings.isValid(name) && fileStore.fetchInfo().isDirectory()) {
         isValid = !name.startsWith(".");
      }
      return isValid;
   }

   @Override
   public boolean isValidFile(IFileStore fileStore) {
      boolean isValid = false;
      String name = fileStore.getName();
      if (Strings.isValid(name) && !fileStore.fetchInfo().isDirectory()) {
         isValid = true;
      }
      return isValid;
   }

   @Override
   public boolean hasValidContent(CharBuffer fileBuffer) {
      return fileBuffer != null && fileBuffer.length() > 0;
   }

   @Override
   public ResourceIdentifier getIdentifier(IFileStore fileStore, CharBuffer fileBuffer) throws Exception {
      return new ResourceIdentifier(fileStore.getName());
   }

   @Override
   public ArtifactTypeToken getTraceUnitType(String name, CharBuffer fileBuffer) {
      return CoreArtifactTypes.CodeUnit;
   }

}
