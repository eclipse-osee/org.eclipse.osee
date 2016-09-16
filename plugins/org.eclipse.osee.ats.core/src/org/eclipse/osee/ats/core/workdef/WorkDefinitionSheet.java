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
import org.eclipse.osee.framework.core.data.ArtifactToken;

/**
 * @author Donald G. Dunne
 */
public class WorkDefinitionSheet {

   public File file;
   public String name;
   private final ArtifactToken token;

   public WorkDefinitionSheet(String name, File file) {
      this(name, file, null);
   }

   public WorkDefinitionSheet(String name, File file, ArtifactToken asToken) {
      super();
      this.file = file;
      this.name = name;
      this.token = asToken;
   }

   public File getFile() {
      return file;
   }

   public String getName() {
      return name;
   }

   @Override
   public String toString() {
      return String.format("%s   - file[%s]", name, file);
   }

   public ArtifactToken getToken() {
      return token;
   }
}
