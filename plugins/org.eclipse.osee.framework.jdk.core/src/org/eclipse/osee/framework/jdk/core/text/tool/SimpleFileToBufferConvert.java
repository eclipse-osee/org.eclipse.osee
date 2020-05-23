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

package org.eclipse.osee.framework.jdk.core.text.tool;

import java.io.File;
import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.text.FileToBufferConvert;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class SimpleFileToBufferConvert implements FileToBufferConvert {

   @Override
   public CharSequence fileToCharSequence(File file) throws IOException {
      return Lib.fileToCharBuffer(file);
   }
}