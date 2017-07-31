/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.workdef.provider;

import java.io.File;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.dsl.atsDsl.StateDef;
import org.eclipse.osee.ats.dsl.atsDsl.WorkDef;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.ats.workdef.AtsDslUtil;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.ws.AWorkspace;

/**
 * Loads Work Definitions from database or file ATS DSL
 *
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionImporter {

   private static AtsWorkDefinitionImporter provider = new AtsWorkDefinitionImporter();

   public static AtsWorkDefinitionImporter get() {
      return provider;
   }

   /**
    * If sheet has WorkDef defined, create artifact and import string. Return artifact, else return null.
    */
   public Artifact importWorkDefinitionSheetToDb(WorkDefinitionSheet sheet, XResultData resultData, Set<String> stateNames, IAtsChangeSet changes) throws OseeCoreException {
      String modelName = sheet.getFile().getName();
      // Prove that can convert to atsDsl
      AtsDsl atsDsl = AtsDslUtil.getFromSheet(modelName, sheet);
      if (atsDsl.getWorkDef() != null) {
         // Use original xml to store in artifact so no conversion happens
         String workDefXml = AtsDslUtil.getString(sheet);
         Artifact artifact = importWorkDefinitionToDb(workDefXml, sheet.getName(), sheet.getName(), sheet.getToken(),
            resultData, changes);
         if (resultData.getNumErrors() > 0) {
            throw new OseeStateException("Error importing WorkDefinitionSheet [%s] into database [%s]", sheet.getName(),
               resultData.toString());
         }
         for (WorkDef workDef : atsDsl.getWorkDef()) {
            for (StateDef state : workDef.getStates()) {
               stateNames.add(Strings.unquote(state.getName()));
            }
         }
         return artifact;
      }
      return null;
   }

   public Artifact importWorkDefinitionToDb(String workDefXml, String workDefName, String sheetName, ArtifactToken token, XResultData resultData, IAtsChangeSet changes) throws OseeCoreException {
      Artifact artifact = null;
      try {
         if (token != null) {
            artifact = ArtifactQuery.getArtifactFromToken(token);
         } else {
            artifact = ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition, sheetName,
               AtsClientService.get().getAtsBranch());
         }
      } catch (ArtifactDoesNotExist ex) {
         // do nothing; this is what we want
      }
      if (artifact != null) {
         String importStr = String.format("WorkDefinition [%s] already loaded into database", workDefName);
         if (!MessageDialog.openConfirm(AWorkbench.getActiveShell(), "Overwrite Work Definition",
            importStr + "\n\nOverwrite?")) {
            OseeLog.log(Activator.class, Level.INFO, importStr + "...skipping");
            resultData.log(importStr + "...skipping");
            return artifact;
         } else {
            resultData.log(importStr + "...overwriting");
         }
      } else {
         resultData.log(String.format("Imported new WorkDefinition [%s]", workDefName));
         artifact = ArtifactTypeManager.addArtifact(AtsArtifactTypes.WorkDefinition,
            AtsClientService.get().getAtsBranch(), sheetName);
      }
      artifact.setSoleAttributeValue(AtsAttributeTypes.DslSheet, workDefXml);
      changes.add(artifact);
      return artifact;
   }

   public void convertAndOpenAtsDsl(Artifact workDefArt, XResultData resultData) throws OseeCoreException {
      String dslText = workDefArt.getSoleAttributeValue(AtsAttributeTypes.DslSheet, "");
      String filename = workDefArt.getName() + ".ats";
      File file = OseeData.getFile(filename);
      try {
         Lib.writeStringToFile(dslText, file);
         final IFile iFile = OseeData.getIFile(filename);
         Displays.ensureInDisplayThread(new Runnable() {

            @Override
            public void run() {
               AWorkspace.openEditor(iFile);
            }

         });
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public void convertAndOpenAtsDsl(IAtsWorkDefinition workDef, XResultData resultData, String filename) throws OseeCoreException {
      try {
         String storageStr = AtsClientService.get().getWorkDefinitionService().getStorageString(workDef, resultData);
         IFile iFile = OseeData.getIFile(filename);
         Lib.writeStringToFile(storageStr, AWorkspace.iFileToFile(iFile));
         AWorkspace.openEditor(iFile);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

}
