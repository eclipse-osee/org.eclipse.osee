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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter for filtering files that end with a specific string.
 * 
 * @author Robert A. Fisher
 */
public class ExtensionFilter implements FileFilter {
   private final String extension;

   /**
    * @param extension The string to match the end of the filenames against.
    * @throws IllegalArgumentException if <code>extension</code> is null.
    */
   public ExtensionFilter(String extension) {
      if (extension == null) throw new IllegalArgumentException("extension must not be null");
      this.extension = extension;
   }

   public boolean accept(File file) {
      return file.isFile() && file.getName().endsWith(extension);
   }
}
