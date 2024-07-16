/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.type.LinkType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author John Misinco
 */
public class FixEmbeddedLinksBlam extends AbstractBlam {

   private static final String BRANCH = "Branch Input";

   @Override
   public String getXWidgetsXml() {
      return String.format("<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"%s\"/></xWidgets>",
         BRANCH);
   }

   @Override
   public String getDescriptionUsage() {
      return "For a given branch, locate all WholeWordContent attributes and replace branchUuids with that of the selected branch";
   }

   @Override
   public String getName() {
      return "Fix Embedded Links";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      BranchId branch = variableMap.getBranch(BRANCH);
      QueryBuilderArtifact queryBuilder = ArtifactQuery.createQueryBuilder(branch);
      queryBuilder.andExists(CoreAttributeTypes.WholeWordContent);
      SkynetTransaction tx = TransactionManager.createTransaction(branch, "Update embedded links");
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      for (Artifact artifact : queryBuilder.getResults()) {
         if (artifact.isAttributeTypeValid(CoreAttributeTypes.WholeWordContent)) {
            String content = artifact.getSoleAttributeValueAsString(CoreAttributeTypes.WholeWordContent, "");
            if (Strings.isValid(content)) {
               Set<String> unknownGuids = new HashSet<>();
               content = WordMlLinkHandler.link(linkType, artifact, content, unknownGuids);
               artifact.setSoleAttributeFromString(CoreAttributeTypes.WholeWordContent, content);
               artifact.persist(tx);
               if (!unknownGuids.isEmpty()) {
                  log(String.format("Unknown guids found in [%s] - %s", artifact,
                     org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", unknownGuids)));
               }
            }
         }
      }
      tx.execute();
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.TOP_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

}
