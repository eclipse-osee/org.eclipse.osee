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
package org.eclipse.osee.define.blam.operation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler.MatchRange;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Megumi Telles
 */
public class FindErroneousEmbeddedLinksBlam extends AbstractBlam {

   private static final String BRANCH = "Branch Input";

   @Override
   public String getXWidgetsXml() throws OseeCoreException {
      return String.format("<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"%s\"/></xWidgets>",
         BRANCH);
   }

   @Override
   public String getDescriptionUsage() {
      return "For a given branch, locate all WordTemplateContent attributes and list those artifacts with hyperlinks pointing to itself";
   }

   @Override
   public String getName() {
      return "Find Erroneous Hyperlinks";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      BranchId branch = variableMap.getBranch(BRANCH);
      QueryBuilderArtifact queryBuilder = ArtifactQuery.createQueryBuilder(branch);
      queryBuilder.andExists(CoreAttributeTypes.WordTemplateContent);
      ResultSet<Artifact> arts = queryBuilder.getResults();
      Iterator<Artifact> artIter = arts.iterator();
      while (artIter.hasNext()) {
         Artifact artifact = artIter.next();
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.WordTemplateContent)) {
            try {
               String content = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.WordTemplateContent, "");
               if (Strings.isValid(content)) {
                  findIncorrectLinks(artifact, content);
               }
            } catch (OseeCoreException ex) {
               logf("Artifact: [%s]: [%s]", artifact.getName(), ex);
            }
         }
         artIter.remove();
      }
   }

   private void findIncorrectLinks(Artifact artifact, String content) {
      Set<String> unknownGuids = new HashSet<>();
      HashCollection<String, MatchRange> links = WordMlLinkHandler.getLinks(content);
      if (!links.isEmpty()) {
         unknownGuids.addAll(links.keySet());
      }
      
      Iterator<String> guidIter = unknownGuids.iterator();
      while (guidIter.hasNext()) {
         // Pointing to itself
         String linkGuid = guidIter.next();
         logLinksToSelf(artifact, linkGuid);
         logInvalidGuidLink(artifact, linkGuid);         
         
         guidIter.remove();
      }
   }

   private void logInvalidGuidLink(Artifact artifact, String linkGuid) {
      try {
         ArtifactQuery.getArtifactFromId(linkGuid, artifact.getBranch());
      } catch (Exception e) {
         logf("Warning: this artifact contains an invalid link - Artifact: [%s] Guid: [%s] Invalid Link Guid: [%s]", artifact.getName(), artifact.getGuid(), linkGuid);
      }
   }

   private void logLinksToSelf(Artifact artifact, String linkGuid) {
      if (linkGuid.equals(artifact.getGuid())) {
         logf("Warning: this artifact contains a link pointing to itself - Artifact: [%s] Guid: [%s]", artifact.getName(), artifact.getGuid());
      }
   }

   @Override
   public Collection<String> getCategories() {
      return Collections.singletonList("Util");
   }

}
