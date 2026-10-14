/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.ats.ide.util.Import;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.data.AtsUserGroups;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughArtifactCollector;
import org.eclipse.osee.framework.skynet.core.importing.operations.RoughToRealArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.operations.SourceToRoughArtifactOperation;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory.ArtifactCreationStrategy;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerDragAndDrop;
import org.osgi.service.component.annotations.Component;

/**
 * See description usage below.
 *
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ImportGeneralDocumentsBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Import General Document(s)";
   static final String FILE_SELECTION = "Import Directory";
   static final String PARENT_ARTIFACT = "Parent Artifact";

   BranchToken branch = null;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public ImportGeneralDocumentsBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andDirSelWidget(FILE_SELECTION).andToolTip("Select a Word XML file to import");
      wb.andListDropWidget(PARENT_ARTIFACT);
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      String fileLocation = variableMap.getString(FILE_SELECTION);
      Artifact parentImportArt = variableMap.getArtifact(PARENT_ARTIFACT);

      File importDir = new File(fileLocation);
      RelationTypeSide relType = CoreRelationTypes.DefaultHierarchical_Child;

      XResultData rd = importGeneralDocuments(parentImportArt, importDir, relType, new XResultData());
      log(rd.toString());
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   public static XResultData importGeneralDocuments(Artifact parentImportArt, File importDir, RelationTypeSide relType,
      XResultData rd) {
      try {

         if (parentImportArt == null) {
            rd.error("Parent Import Artifact can not be null");
            return rd;
         }

         SkynetTransaction transaction =
            TransactionManager.createTransaction(parentImportArt.getBranch(), "Import General Document");
         File[] filesInDir = importDir.listFiles();
         if (filesInDir != null) {
            for (File file : filesInDir) {

               System.err.println(file.getAbsolutePath());

               IArtifactExtractor extractor =
                  ArtifactExplorerDragAndDrop.getArtifactExtractor(CoreArtifactTypes.GeneralDocument);
               RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact());
               IArtifactImportResolver resolver =
                  ArtifactResolverFactory.createResolver(ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID,
                     CoreArtifactTypes.GeneralDocument, Arrays.asList(Name), true, false);

               SourceToRoughArtifactOperation sourceToRoughArtifactOperation =
                  new SourceToRoughArtifactOperation(null, extractor, file, collector);
               sourceToRoughArtifactOperation.run(null);
               RoughToRealArtifactOperation roughToRealArtifactOperation =
                  new RoughToRealArtifactOperation(transaction, parentImportArt, collector, resolver, false, extractor);
               roughToRealArtifactOperation.setAddRelation(false);
               IStatus run = roughToRealArtifactOperation.run(null);
               if (!run.isOK()) {
                  rd.error(run.toString());
                  return rd;
               } else {
                  Artifact supportingArt = roughToRealArtifactOperation.getCreatedArtifacts().iterator().next();
                  transaction.addArtifact(supportingArt);
                  parentImportArt.addRelation(relType, supportingArt);
                  transaction.addArtifact(parentImportArt);
               }
            }
         }
         TransactionToken tx = transaction.execute();
         rd.setTxId(tx.getIdString());
      } catch (NumberFormatException ex) {
         rd.error(ex.toString());
      }
      return rd;
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(AtsUserGroups.AtsAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public String getDescriptionUsage() {
      return "Imports one or more General Documents to a branch as child of artifact.  This is ONLY for General Documents.  " //
         + "Use OSEE Import to handle other types of imports.";
   }

}
