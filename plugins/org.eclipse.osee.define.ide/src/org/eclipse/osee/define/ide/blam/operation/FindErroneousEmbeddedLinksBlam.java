/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.blam.operation;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler.MatchRange;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Megumi Telles
 */
public class FindErroneousEmbeddedLinksBlam extends AbstractBlam {

   private static final String BRANCH = "Branch Input";
   private static final String ARTIFACT_TYPES = "Artifact Types";

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append(
         String.format("<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"%s\"/>", BRANCH));
      builder.append(String.format(
         "<XWidget xwidgetType=\"XArtifactTypeMultiChoiceSelect\" displayName=\"Artifact Types\" />", ARTIFACT_TYPES));
      builder.append("</xWidgets>");
      return builder.toString();
   }

   @Override
   public String getDescriptionUsage() {
      return "For a given branch, locate all WordTemplateContent attributes and list those artifacts with invalid hyperlinks";
   }

   @Override
   public String getName() {
      return "Find Erroneous Hyperlinks";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
      Date date = new Date();
      File file = OseeData.getFile("INVALID_LINKS_" + dateFormat.format(date) + ".xml");
      ISheetWriter excelWriter = new ExcelXmlWriter(file);

      BranchId branch = variableMap.getBranch(BRANCH);
      List<IArtifactType> artifactTypes = variableMap.getArtifactTypes(ARTIFACT_TYPES);

      QueryBuilderArtifact queryBuilder = ArtifactQuery.createQueryBuilder(branch);
      queryBuilder.andExists(CoreAttributeTypes.WordTemplateContent);
      ResultSet<Artifact> arts = queryBuilder.getResults();
      Iterator<Artifact> artIter = arts.iterator();

      excelWriter.startSheet("Invalid Links", 6);
      excelWriter.writeRow("Type", "Artifact", "Artifact Type", "Guid", "Subsystem", "Invalid Link Guid");
      while (artIter.hasNext()) {
         Artifact artifact = artIter.next();
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.WordTemplateContent) && //
            isOfTypes(artifactTypes, artifact)) {
            try {
               String content = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.WordTemplateContent, "");
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

   private boolean isOfTypes(List<IArtifactType> artifactTypes, Artifact artifact) {
      for (IArtifactType type : artifactTypes) {
         if (artifact.isOfType(type)) {
            return true;
         }
      }
      return false;
   }

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
         // Pointing to itself
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

   @Override
   public Collection<String> getCategories() {
      return Collections.singletonList("Define.Publish.Check");
   }

}
