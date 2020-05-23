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

package org.eclipse.osee.framework.ui.skynet.render;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public abstract class WordRenderer extends FileSystemRenderer {

   public WordRenderer(Map<RendererOption, Object> rendererOptions) {
      super(rendererOptions);
   }

   public WordRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   // We need MS Word, so look for the program that is for .doc files
   private static final Program wordApp = Program.findProgram("doc");

   @Override
   public String getName() {
      return "MS Word Edit";
   }

   @Override
   public String getAssociatedExtension(Artifact artifact) {
      return "xml";
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) {
      if (wordApp == null) {
         throw new OseeStateException("No program associated with the extension .doc");
      }
      return wordApp;
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }
}