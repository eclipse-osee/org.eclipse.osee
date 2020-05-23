/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.File;

/**
 * @author Ken J. Aguilar
 */
public class FileChangeEvent {
   private final File file;
   private final FileChangeType changeType;

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
