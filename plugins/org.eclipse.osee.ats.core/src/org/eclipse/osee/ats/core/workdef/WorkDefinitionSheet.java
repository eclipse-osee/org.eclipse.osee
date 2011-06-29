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
package org.eclipse.osee.ats.core.workdef;

import java.io.File;

public class WorkDefinitionSheet {

   public File file;
   public String name;
   public String legacyOverrideId;

   public WorkDefinitionSheet(String name, String legacyOverrideId, File file) {
      super();
      this.file = file;
      this.name = name;
      this.legacyOverrideId = legacyOverrideId;
   }

   public File getFile() {
      return file;
   }

   public String getName() {
      return name;
   }

   public String getLegacyOverrideId() {
      return legacyOverrideId;
   }

   @Override
   public String toString() {
      return String.format("%s filename[%s] overrideId[%s]", name, file, legacyOverrideId);
   }
}
