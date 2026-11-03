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

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WholeWordContent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.core.widget.XWidgetLabels;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.skynet.core.linking.WordMlLinkHandler;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;

/**
 * @author John Misinco
 */
@Component(service = AbstractBlam.class, immediate = true)
public class FixEmbeddedLinksBlam extends AbstractBlam {

   private static final String BLAM_NAME = "Fix Embedded Links";

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public FixEmbeddedLinksBlam() {
      super(BLAM_NAME, CoreImage.GEAR);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andBranchSelWidget();
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

      BranchId branch = variableMap.getBranch(XWidgetLabels.BRANCH);
      QueryBuilderArtifact queryBuilder = ArtifactQuery.createQueryBuilder(branch);
      queryBuilder.andExists(WholeWordContent);
      SkynetTransaction tx = TransactionManager.createTransaction(branch, "Update embedded links");
      LinkType linkType = LinkType.OSEE_SERVER_LINK;
      for (Artifact artifact : queryBuilder.getResults()) {
         if (artifact.isAttributeTypeValid(WholeWordContent)) {
            String content = artifact.getSoleAttributeValueAsString(WholeWordContent, "");
            if (Strings.isValid(content)) {
               Set<String> unknownGuids = new HashSet<>();
               content = WordMlLinkHandler.link(linkType, artifact, content, unknownGuids);
               artifact.setSoleAttributeFromString(WholeWordContent, content);
               artifact.persist(tx);
               if (!unknownGuids.isEmpty()) {
                  logf("Unknown guids found in [%s] - %s", artifact,
                     org.eclipse.osee.framework.jdk.core.util.Collections.toString(", ", unknownGuids));
               }
            }
         }
      }
      tx.execute();
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.OseeAdmin);
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN);
   }

   @Override
   public String getDescriptionUsage() {
      return "For a given branch, locate all WholeWordContent attributes and replace branchUuids with that of the selected branch";
   }

}
