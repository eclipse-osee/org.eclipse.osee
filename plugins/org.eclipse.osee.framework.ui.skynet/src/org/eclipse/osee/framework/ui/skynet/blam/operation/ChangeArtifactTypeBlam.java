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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.ChangeArtifactType;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * Changes the descriptor type of an artifact to the provided descriptor.
 *
 * @author Jeff C. Phillips
 * @author Karol M. Wilk
 */
public class ChangeArtifactTypeBlam extends AbstractBlam {

   private static final String description =
      "Start by drag-and-drop or by pasting GUIDs of artifacts. Log what the previous type of each artifact was because that information is loss after running this blam";

   public ChangeArtifactTypeBlam() {
      super(null, description, BlamUiSource.FILE);
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      ChangeArtifactType.changeArtifactType(variableMap.getArtifacts("artifacts"),
         variableMap.getArtifactType("New Artifact Type"), true);
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}