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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workdef.ConvertAtsDslToWorkDefinition;
import org.eclipse.osee.ats.core.workdef.ModelUtil;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinitionSheet;
import org.eclipse.osee.ats.core.workdef.provider.AtsWorkDefinitionProviderCore;
import org.eclipse.osee.ats.dsl.atsDsl.AtsDsl;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.workdef.config.ImportAIsAndTeamDefinitionsToDb;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.ws.AWorkspace;

/**
 * Loads Work Definitions from database or file ATS DSL
 * 
 * @author Donald G. Dunne
 */
public class AtsWorkDefinitionProvider {

   private static AtsWorkDefinitionProvider provider = new AtsWorkDefinitionProvider();

   public static AtsWorkDefinitionProvider get() {
      return provider;
   }

   public WorkDefinition getWorkFlowDefinition(String id) throws OseeCoreException {
      WorkDefinition workDef = loadWorkDefinitionFromArtifact(id);
      return workDef;
   }

   public void importAIsAndTeamsToDb(WorkDefinitionSheet sheet, SkynetTransaction transaction) throws OseeCoreException {
      String modelName = sheet.getFile().getName();
      AtsDsl atsDsl = loadAtsDslFromFile(modelName, sheet);
      ImportAIsAndTeamDefinitionsToDb importer = new ImportAIsAndTeamDefinitionsToDb(modelName, atsDsl, transaction);
      importer.execute();
   }

   public Artifact importWorkDefinitionSheetToDb(WorkDefinitionSheet sheet, XResultData resultData, boolean onlyWorkDefinitions, SkynetTransaction transaction) throws OseeCoreException {
      String modelName = sheet.getFile().getName();
      // Prove that can convert to atsDsl
      AtsDsl atsDsl = loadAtsDslFromFile(modelName, sheet);
      ConvertAtsDslToWorkDefinition converter = new ConvertAtsDslToWorkDefinition(modelName, atsDsl);
      WorkDefinition workDef = converter.convert();
      Artifact artifact = null;
      if (workDef == null && atsDsl.getWorkDef() != null) {
         throw new OseeStateException("WorkDefinitionSheet [%s] unexpectedly returned null WorkDef", sheet.getName());
      }
      if (workDef != null) {
         if (!sheet.getName().equals(workDef.getName())) {
            throw new OseeStateException("WorkDefinitionSheet [%s] internal name [%s] does not match sheet name",
               sheet.getName(), workDef.getName());
         }
         if (!sheet.getName().equals(workDef.getIds().iterator().next())) {
            throw new OseeStateException("WorkDefinitionSheet [%s] internal id [%s] does not match sheet name", sheet,
               workDef.getIds().iterator().next());
         }
         // Use original xml to store in artifact so no conversion happens
         String workDefXml = loadWorkFlowDefinitionStringFromFile(sheet);
         artifact = importWorkDefinitionToDb(workDefXml, workDef.getName(), sheet.getName(), resultData, transaction);
         if (resultData.getNumErrors() > 0) {
            throw new OseeStateException("Error importing WorkDefinitionSheet [%s] into database [%s]",
               sheet.getName(), resultData.toString());
         }
      }
      if (!onlyWorkDefinitions) {
         ImportAIsAndTeamDefinitionsToDb importer = new ImportAIsAndTeamDefinitionsToDb(modelName, atsDsl, transaction);
         importer.execute();
      }

      return artifact;
   }

   public Artifact importWorkDefinitionToDb(String workDefXml, String workDefName, String sheetName, XResultData resultData, SkynetTransaction transaction) throws OseeCoreException {
      Artifact artifact = null;
      try {
         artifact =
            ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.WorkDefinition, sheetName,
               AtsUtilCore.getAtsBranch());
      } catch (ArtifactDoesNotExist ex) {
         // do nothing; this is what we want
      }
      if (artifact != null) {
         String importStr = String.format("WorkDefinition [%s] already loaded into database", workDefName);
         if (!MessageDialog.openConfirm(AWorkbench.getActiveShell(), "Overwrite Work Definition",
            importStr + "\n\nOverwrite?")) {
            OseeLog.log(AtsPlugin.class, Level.INFO, importStr + "...skipping");
            resultData.log(importStr + "...skipping");
            return artifact;
         } else {
            resultData.log(importStr + "...overwriting");
         }
      } else {
         resultData.log(String.format("Imported new WorkDefinition [%s]", workDefName));
         artifact =
            ArtifactTypeManager.addArtifact(AtsArtifactTypes.WorkDefinition, AtsUtilCore.getAtsBranch(), sheetName);
      }
      artifact.setSoleAttributeValue(AtsAttributeTypes.DslSheet, workDefXml);
      artifact.persist(transaction);

      return artifact;
   }
   private static class StringOutputStream extends OutputStream {
      private final StringBuilder string = new StringBuilder();

      @Override
      public void write(int b) {
         this.string.append((char) b);
      }

      @Override
      public String toString() {
         return this.string.toString();
      }
   };

   public String workFlowDefinitionToString(WorkDefinition workDef, XResultData resultData) throws OseeCoreException {
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(workDef, resultData);
      AtsDsl atsDsl = converter.convert(workDef.getName());
      try {
         StringOutputStream writer = new StringOutputStream();
         ModelUtil.saveModel(atsDsl, "ats:/mock" + Lib.getDateTimeString() + ".ats", writer, false);
         return writer.toString();
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public String loadWorkFlowDefinitionStringFromFile(WorkDefinitionSheet sheet) throws OseeCoreException {
      if (!sheet.getFile().exists()) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, String.format("WorkDefinition [%s]", sheet));
         return null;
      }
      try {
         return Lib.fileToString(sheet.getFile());
      } catch (IOException ex) {
         throw new OseeWrappedException(String.format("Error loading workdefinition sheet[%s]", sheet), ex);
      }
   }

   public AtsDsl loadAtsDslFromFile(String modelName, WorkDefinitionSheet sheet) {
      try {
         AtsDsl atsDsl =
            AtsWorkDefinitionProviderCore.loadAtsDsl(modelName, loadWorkFlowDefinitionStringFromFile(sheet));
         return atsDsl;
      } catch (Exception ex) {
         throw new WrappedException(ex);
      }
   }

   public WorkDefinition loadWorkFlowDefinitionFromFile(WorkDefinitionSheet sheet) throws OseeWrappedException {
      try {
         String modelName = sheet.getFile().getName();
         AtsDsl atsDsl = loadAtsDslFromFile(modelName, sheet);

         ConvertAtsDslToWorkDefinition converter = new ConvertAtsDslToWorkDefinition(modelName, atsDsl);
         WorkDefinition workDef = converter.convert();
         workDef.setDescription(String.format("Loaded WorkDefinitionSheet [%s]", sheet));
         return workDef;
      } catch (Exception ex) {
         throw new OseeWrappedException(String.format("Error loading AtsDsl [%s] from file [%s]", sheet.getName(),
            sheet.getFile()), ex);
      }
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
      AtsDsl atsDsl = AtsWorkDefinitionProviderCore.loadAtsDsl(modelName, modelText);
      ConvertAtsDslToWorkDefinition converter = new ConvertAtsDslToWorkDefinition(modelName, atsDsl);
      return converter.convert();
   };

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

   public void convertAndOpenAtsDsl(WorkDefinition workDef, XResultData resultData, String filename) throws OseeCoreException {
      ConvertWorkDefinitionToAtsDsl converter = new ConvertWorkDefinitionToAtsDsl(workDef, resultData);
      AtsDsl atsDsl = converter.convert(filename.replaceFirst("\\.ats", ""));
      File file = OseeData.getFile(filename);
      try {
         FileOutputStream outputStream = new FileOutputStream(file);
         ModelUtil.saveModel(atsDsl, "ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", outputStream, false);
         String contents = Lib.fileToString(file);
         Lib.writeStringToFile(contents, file);
         IFile iFile = OseeData.getIFile(filename);
         AWorkspace.openEditor(iFile);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public void convertAndOpenAIandTeamAtsDsl(XResultData resultData) throws OseeCoreException {
      ConvertAIsAndTeamsToAtsDsl converter = new ConvertAIsAndTeamsToAtsDsl(resultData);
      AtsDsl atsDsl = converter.convert("AIsAndTeams");
      String filename = "AIsAndTeams.ats";
      File file = OseeData.getFile("AIsAndTeams.ats");
      try {
         FileOutputStream outputStream = new FileOutputStream(file);
         ModelUtil.saveModel(atsDsl, "ats:/ats_fileanme" + Lib.getDateTimeString() + ".ats", outputStream, false);
         String contents = Lib.fileToString(file);

         //         contents = cleanupContents(atsDsl, null, contents);

         Lib.writeStringToFile(contents, file);
         IFile iFile = OseeData.getIFile(filename);
         AWorkspace.openEditor(iFile);
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

}
