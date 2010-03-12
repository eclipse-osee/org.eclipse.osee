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
package org.eclipse.osee.framework.skynet.core.utility;

import java.io.File;

/**
 * @author Ken J. Aguilar
 */
public class FileChangeEvent {
   private final File file;
   private final FileChangeType changeType;

   /**
    * @param file
    * @param changeType
    */
   public FileChangeEvent(File file, FileChangeType changeType) {
      this.file = file;
      this.changeType = changeType;
   }

   /**
    * @return the file
    */
   public File getFile() {
      return file;
   }

   /**
    * @return the changeType
    */
   public FileChangeType getChangeType() {
      return changeType;
   }

}
