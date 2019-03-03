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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifactType extends AbstractBlam {
   private boolean convertArtifacts;

   @Override
   public String getName() {
      return "Purge Artifact Type";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Collection<? extends ArtifactTypeToken> purgeArtifactTypes =
         variableMap.getCollection(ArtifactType.class, "Artifact Type(s) to purge");
      convertArtifacts = variableMap.getBoolean("Convert Artifacts");
      ArtifactTypeToken newArtifactType = convertArtifacts ? variableMap.getArtifactType("New Artifact Type") : null;

      ArtifactTypeManager.purgeArtifactTypesWithCheck(purgeArtifactTypes, newArtifactType);
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XArtifactTypeMultiChoiceSelect\" displayName=\"Artifact Type(s) to purge\" />" + //
         "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Convert Artifacts\" />" + //
         "<XWidget xwidgetType=\"XArtifactTypeComboViewer\" displayName=\"New Artifact Type\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge an artifact type.  Will find artifacts (if any) of this type on all branches and switch their type to the specified type.  Then purge the artifact type ";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

   @Override
   public String getTarget() {
      return TARGET_ALL;
   }

}