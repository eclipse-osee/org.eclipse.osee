/******************************************************************************
 *
 * Copyright (c) 2025 Boeing
 *
 * DISTRIBUTION STATEMENT D - Distribution authorized to DoD and U.S. DoD
 *   contractors only (Critical Technology), 11 June 2014. Other requests for
 *   this document will be referred to COMNAVAIRSYSCOM (PEO(T) PMA-265)
 *
 * WARNING - This document contains technical data whose export is restricted by
 *   the Arms Export Control Act (Title 22, U.S.C., Sec. 2751 et seq.) or
 *   Executive Order 12470. Violators of these export laws are subject to
 *   severe criminal penalties.
 *
 * EXPORT CONTROLLED - This technical data or software is subject to the U.S.
 *   International Traffic in Arms Regulations (ITAR), (22 C.F.R. Parts 120-130).
 *   Export, re-export, retransfer, or access is prohibited without
 *   authorization from the U.S. Department of State.
 *
 * DESTRUCTION NOTICE - For classified documents, follow the procedures in
 *   DOD 5220.22-M, Industrial Security Manual, Chapter 5, Section 7 Disposition
 *   and Retention, or DOD 5200.1-R, Information Security Program Regulation,
 *   Chapter 6, paragraph C6.7 Disposition and Destruction of Classified Material.
 *   For unclassified, limited documents destroy by any method that will prevent
 *   disclosure of contents or reconstruction of the document.
 *
 *****************************************************************************/
package org.eclipse.osee.ats.ide.util.Import;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
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
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.XFileTextWithSelectionDialog.Type;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.builder.XWidgetBuilder;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetRendererItem;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * See description usage below.
 *
 * @author Donald G. Dunne
 */
public class ImportGeneralDocuments extends AbstractBlam {

   private static final String FILE_SELECTION = "Import Directory";
   private static final String PARENT_ARTIFACT = "Parent Artifact";

   BranchToken branch = null;

   @Override
   public String getName() {
      return "Import General Document(s)";
   }

   @Override
   public String getDescriptionUsage() {
      return "Imports one or more General Documents to a branch as child of artifact.  This is ONLY for General Documents.  " //
         + "Use OSEE Import to handle other types of imports.";
   }

   @Override
   public List<XWidgetRendererItem> getXWidgetItems() {
      XWidgetBuilder wb = new XWidgetBuilder();
      wb.andWidget(FILE_SELECTION, "XFileTextWithSelectionDialog").andToolTip(
         "Select a Word XML file to import").andComposite(3).endWidget();
      wb.andWidget(PARENT_ARTIFACT, "XListDropViewer").endWidget();
      return wb.getItems();
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_UTIL, XNavItemCat.OSEE_ADMIN);
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      String fileLocation = variableMap.getString(FILE_SELECTION);
      Artifact parentImportArt = variableMap.getArtifact(PARENT_ARTIFACT);

      File importDir = new File(fileLocation);
      RelationTypeSide relType = CoreRelationTypes.DefaultHierarchical_Child;

      XResultData rd = importGeneralDocuments(parentImportArt, importDir, relType, new XResultData());
      log(rd.toString());
   }

   public static XResultData importGeneralDocuments(Artifact parentImportArt, File importDir, RelationTypeSide relType,
      XResultData rd) {
      try {

         if (parentImportArt == null) {
            rd.error("Parent Import Artifact can not be null");
            return rd;
         }

         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), "Import General Document");
         File[] filesInDir = importDir.listFiles();
         if (filesInDir != null) {
            for (File file : filesInDir) {

               System.err.println(file.getAbsolutePath());

               IArtifactExtractor extractor =
                  ArtifactExplorerDragAndDrop.getArtifactExtractor(CoreArtifactTypes.GeneralDocument);
               RoughArtifactCollector collector = new RoughArtifactCollector(new RoughArtifact());
               IArtifactImportResolver resolver =
                  ArtifactResolverFactory.createResolver(ArtifactCreationStrategy.CREATE_ON_NEW_ART_GUID,
                     CoreArtifactTypes.GeneralDocument, Arrays.asList(CoreAttributeTypes.Name), true, false);

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
   public void widgetCreating(XWidget xWidget, FormToolkit toolkit, Artifact art,
      SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener xModListener, boolean isEditable) {
      super.widgetCreating(xWidget, toolkit, art, dynamicXWidgetLayout, xModListener, isEditable);
      if (xWidget.getLabel().equals(FILE_SELECTION)) {
         XFileTextWithSelectionDialog widget = (XFileTextWithSelectionDialog) xWidget;
         widget.setFileType(Type.Directory);
      }
   }

}
