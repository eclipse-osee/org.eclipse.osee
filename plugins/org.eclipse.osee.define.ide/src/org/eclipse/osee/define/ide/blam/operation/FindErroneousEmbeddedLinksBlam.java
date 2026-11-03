/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.ide.blam.operation;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WordTemplateContent;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler.MatchRange;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;
import org.osgi.service.component.annotations.Component;

/**
 * @author Megumi Telles
 */
@Component(service = AbstractBlam.class, immediate = true)
public class FindErroneousEmbeddedLinksBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Find Erroneous Hyperlinks";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public FindErroneousEmbeddedLinksBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andBranchSelWidget(XWidgetLabels.BRANCH);
      wb.andArtifactTypeWidget(XWidgetLabels.ARTIFACT_TYPES).andValueMultiSelect();
      return wb.getXWidgetDatas();
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }

      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      Date date = new Date();
      BranchId branch = variableMap.getBranch(XWidgetLabels.BRANCH);
      ArtifactTypeToken[] artifactTypes =
         variableMap.getArtifactTypes(XWidgetLabels.ARTIFACT_TYPES).stream().toArray(ArtifactTypeToken[]::new);

      File file = OseeData.getFile(String.format("INVALID_LINKS_%s.xml", dateFormat.format(date)));
      ISheetWriter excelWriter = new ExcelXmlWriter(file);

      QueryBuilderArtifact queryBuilder = ArtifactQuery.createQueryBuilder(branch);
      queryBuilder.andExists(WordTemplateContent);
      ResultSet<Artifact> arts = queryBuilder.getResults();
      Iterator<Artifact> artIter = arts.iterator();

      excelWriter.startSheet("Invalid Links", 6);
      excelWriter.writeRow("Type", "Artifact", XWidgetLabels.ARTIFACT_TYPE, "Guid", "Subsystem", "Invalid Link Guid");
      while (artIter.hasNext()) {
         Artifact artifact = artIter.next();
         if (artifact.isAttributeTypeValid(WordTemplateContent) && artifact.isOfType(artifactTypes)) {
            try {
               String content = artifact.getSoleAttributeValueAsString(WordTemplateContent, "");
               if (Strings.isValid(content)) {
                  findIncorrectLinks(artifact, content, excelWriter);
               }
            } catch (OseeCoreException ex) {
               logf("Artifact: [%s]: [%s]", artifact.getName(), ex);
            }
         }
         artIter.remove();
      }
      excelWriter.endSheet();
      excelWriter.endWorkbook();
      Program.launch(file.getAbsolutePath());
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   private void findIncorrectLinks(Artifact artifact, String content, ISheetWriter excelWriter) throws IOException {
      Set<String> unknownGuids = new HashSet<>();
      HashCollection<String, MatchRange> errorMap = new HashCollection<>();
      HashCollection<String, MatchRange> links = WordMlLinkHandler.getLinks(content, errorMap);
      if (!links.isEmpty()) {
         unknownGuids.addAll(links.keySet());
      }
      findInvalid(artifact, excelWriter, unknownGuids);

      unknownGuids.clear();
      if (!errorMap.isEmpty()) {
         unknownGuids.addAll(errorMap.keySet());
      }
      findInvalid(artifact, excelWriter, unknownGuids);
   }

   private void findInvalid(Artifact artifact, ISheetWriter excelWriter, Set<String> unknownGuids) throws IOException {
      Iterator<String> guidIter = unknownGuids.iterator();
      while (guidIter.hasNext()) {
         String linkGuid = guidIter.next();
         logLinksToSelf(artifact, linkGuid, excelWriter);
         logInvalidGuidLink(artifact, linkGuid, excelWriter);
         guidIter.remove();
      }
   }

   private void logInvalidGuidLink(Artifact artifact, String linkGuid, ISheetWriter excelWriter) throws IOException {
      try {
         ArtifactQuery.getArtifactFromId(linkGuid, artifact.getBranch());
      } catch (Exception e) {
         String subsystem = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.Subsystem, "");
         excelWriter.writeRow("Invalid", artifact.getName(), artifact.getArtifactType().getName(), artifact.getGuid(),
            subsystem, linkGuid == null ? "NULL" : linkGuid);
      }
   }

   private void logLinksToSelf(Artifact artifact, String linkGuid, ISheetWriter excelWriter) throws IOException {
      String subsystem = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.Subsystem, "");
      if (linkGuid != null && linkGuid.equals(artifact.getGuid())) {
         excelWriter.writeRow("Link to Self", artifact.getName(), artifact.getArtifactType().getName(),
            artifact.getGuid(), subsystem, linkGuid);
      }
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_HEALTH);
   }

   @Override
   public String getDescriptionUsage() {
      return "For a given branch, locate all WordTemplateContent attributes and list those artifacts with invalid hyperlinks";
   }

}
