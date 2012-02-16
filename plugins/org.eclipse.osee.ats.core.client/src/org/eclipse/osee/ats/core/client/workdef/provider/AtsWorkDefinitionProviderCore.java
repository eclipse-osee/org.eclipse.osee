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
package org.eclipse.osee.ats.core.client.workdef.provider;

import org.eclipse.osee.ats.core.client.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.client.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.workdef.ConvertAtsDslToWorkDefinition;
import org.eclipse.osee.ats.core.workdef.ModelUtil;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProviderCore {

   private static AtsWorkDefinitionProviderCore provider = new AtsWorkDefinitionProviderCore();

   public static AtsWorkDefinitionProviderCore get() {
      return provider;
   }

   public WorkDefinition getWorkFlowDefinition(String id) throws OseeCoreException {
      WorkDefinition workDef = loadWorkDefinitionFromArtifact(id);
      return workDef;
   }

   private WorkDefinition loadWorkDefinitionFromArtifact(String name) throws OseeCoreException {
      Artifact artifact = null;
      try {
         artifact =
            ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition, name,
               BranchManager.getCommonBranch());
         return loadWorkDefinitionFromArtifact(artifact);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      } catch (Exception ex) {
         throw new OseeWrappedException(String.format("Error loading AtsDsl [%s] from Artifact", name), ex);
      }
      return null;
   }

   public WorkDefinition loadWorkDefinitionFromArtifact(Artifact artifact) throws OseeCoreException {
      String modelText = artifact.getAttributesToString(AtsAttributeTypes.DslSheet);
      String modelName = artifact.getName() + ".ats";
      AtsDsl atsDsl = loadAtsDsl(modelName, modelText);
      ConvertAtsDslToWorkDefinition converter = new ConvertAtsDslToWorkDefinition(modelName, atsDsl);
      return converter.convert();
   };

   public static AtsDsl loadAtsDsl(String name, String modelText) throws OseeCoreException {
      AtsDsl atsDsl = ModelUtil.loadModel(name, modelText);
      return atsDsl;
   }

}
