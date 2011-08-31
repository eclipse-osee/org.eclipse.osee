/*
 * Created on Aug 16, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.dsl.ui.integration.internal;

import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.DEFAULT_OPEN;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.GENERALIZED_EDIT;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.PRODUCE_ATTRIBUTE;
import static org.eclipse.osee.framework.ui.skynet.render.PresentationType.SPECIALIZED_EDIT;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.osee.framework.core.dsl.integration.util.ModelUtil;
import org.eclipse.osee.framework.core.dsl.oseeDsl.OseeDsl;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XArtifactType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XAttributeType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XOseeEnumType;
import org.eclipse.osee.framework.core.dsl.oseeDsl.XRelationType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.HexUtil;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.eclipse.osee.framework.ui.skynet.render.AttributeModifier;
import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.WholeAttributeUpdateOperation;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

public class OseeDslTypeSheetRenderer extends FileSystemRenderer {
   private static final String COMMAND_ID = "org.eclipse.osee.framework.core.dsl.OseeDslTypeSheet.editor.command";

   @Override
   public String getName() {
      return "OseeDsl Editor";
   }

   @Override
   public DefaultArtifactRenderer newInstance() {
      return new OseeDslTypeSheetRenderer();
   }

   @Override
   public int getApplicabilityRating(PresentationType presentationType, IArtifact artifact) throws OseeCoreException {
      Artifact aArtifact = artifact.getFullArtifact();
      if (!presentationType.matches(GENERALIZED_EDIT, PRODUCE_ATTRIBUTE) && !aArtifact.isHistorical()) {
         if (aArtifact.isOfType(CoreArtifactTypes.OseeTypeDefinition)) {
            return ARTIFACT_TYPE_MATCH;
         }
      }
      return NO_MATCH;
   }

   @Override
   public List<String> getCommandIds(CommandGroup commandGroup) {
      ArrayList<String> commandIds = new ArrayList<String>(1);
      if (commandGroup.isEdit()) {
         commandIds.add(COMMAND_ID);
      }
      return commandIds;
   }

   @Override
   public boolean supportsCompare() {
      return true;
   }

   @SuppressWarnings("unused")
   @Override
   public void open(final List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      final PresentationType resultantpresentationType =
         presentationType == DEFAULT_OPEN ? SPECIALIZED_EDIT : presentationType;

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!artifacts.isEmpty()) {
               try {
                  IFile file = renderToFile(artifacts, resultantpresentationType);
                  if (file != null) {
                     IWorkbench workbench = PlatformUI.getWorkbench();
                     IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
                     IDE.openEditor(page, file);
                  }
               } catch (CoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      });
   }

   @SuppressWarnings("unused")
   @Override
   public String getAssociatedExtension(Artifact artifact) throws OseeCoreException {
      return "osee";
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException {
      //      Pattern pattern = Pattern.compile("import\\s\"platform:/plugin/(.*)/support/(.+\\.osee)\"");
      Artifact artifact = artifacts.iterator().next();
      StringBuilder builder = new StringBuilder();
      String sheetData = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.UriGeneralStringData, "");
      //      Matcher m = pattern.matcher(sheetData);
      //      ChangeSet cs = new ChangeSet(sheetData);
      //      String fileName, packageName, change, importStatement;
      //      while (m.find()) {
      //         fileName = m.group(2);
      //         packageName = m.group(1);
      //         importStatement = m.group(0);
      //         change = "";
      //         for (String line : importStatement.split("\n")) {
      //            change += "//" + line + "\n";
      //         }
      //         change += "import \"" + OseeData.getPath() + "\\.working\\Common\\" + fileName + "\"";
      //         cs.replace(m.start(), m.end(), change);
      //      }

      builder.append(sheetData);
      InputStream inputStream = null;
      try {
         inputStream = new ByteArrayInputStream(builder.toString().getBytes("UTF-8"));
      } catch (UnsupportedEncodingException ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return inputStream;
   }

   @Override
   public Program getAssociatedProgram(Artifact artifact) throws OseeCoreException {
      throw new OseeCoreException("should not be called");
   }

   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, Branch branch, PresentationType presentationType) {
      Modifier mod = new Modifier();
      return new WholeAttributeUpdateOperation(file, artifacts.get(0), CoreAttributeTypes.UriGeneralStringData, mod);
   }

   private class Modifier implements AttributeModifier {

      @Override
      public String modifyForSave(Artifact owner, String value) throws OseeCoreException {
         List<Artifact> artifacts =
            ArtifactQuery.getArtifactListFromType(CoreArtifactTypes.OseeTypeDefinition, BranchManager.getCommonBranch());
         StringBuilder combinedSheets = new StringBuilder();
         for (Artifact art : artifacts) {
            String sheetData;
            if (art.equals(owner)) {
               sheetData = value;
            } else {
               sheetData = art.getSoleAttributeValueAsString(CoreAttributeTypes.UriGeneralStringData, "");
            }
            combinedSheets.append(sheetData.replaceAll("import\\s+\"", "// import \""));
         }
         OseeDsl model = ModelUtil.loadModel("osee:/TypeModel.osee", combinedSheets.toString());

         Set<Long> uuids = new HashSet<Long>();
         EList<XArtifactType> artifactTypes = model.getArtifactTypes();
         EList<XAttributeType> attributeTypes = model.getAttributeTypes();
         EList<XOseeEnumType> enumTypes = model.getEnumTypes();
         EList<XRelationType> relationTypes = model.getRelationTypes();

         for (XArtifactType type : artifactTypes) {
            addUuid(HexUtil.toLong(type.getUuid()), uuids);
         }
         for (XAttributeType type : attributeTypes) {
            addUuid(HexUtil.toLong(type.getUuid()), uuids);
         }
         for (XOseeEnumType type : enumTypes) {
            addUuid(HexUtil.toLong(type.getUuid()), uuids);
         }
         for (XRelationType type : relationTypes) {
            addUuid(HexUtil.toLong(type.getUuid()), uuids);
         }

         if (uuids.contains(0L)) {
            throw new OseeStateException("Uuid of 0 is not allowed");
         }

         return value;
      }

      private void addUuid(Long id, Set<Long> set) throws OseeStateException {
         if (set.contains(id)) {
            throw new OseeStateException("Duplicate uuid found: [0x%X]", id);
         } else {
            set.add(id);
         }
      }
   }

}
