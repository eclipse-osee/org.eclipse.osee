/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.framework.ui.skynet.search;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.ArtifactSearchOptions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.QueryBuilderArtifact;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;

/**
 * @author Audrey Denk
 */
final class ArtifactSearch extends AbstractLegacyArtifactSearchQuery {
   private final BranchId branch;
   private final ArtifactSearchOptions options;

   ArtifactSearch(BranchId branch, ArtifactSearchOptions options) {
      super();
      this.options = options;
      this.branch = branch;
   }

   @Override
   public Collection<Artifact> getArtifacts() throws Exception {
      List<ArtifactId> artIds = ServiceUtil.getOseeClient().getArtifactEndpoint(branch).findArtifactIds(options);
      QueryBuilderArtifact query = ArtifactQuery.createQueryBuilder(branch);
      if (!artIds.isEmpty()) {
         query.andIds(artIds);
         query.includeDeleted(options.getIncludeDeleted().areDeletedAllowed());
         return query.getResults().getList();
      }
      return Collections.emptyList();
   }

   @Override
   public String getCriteriaLabel() {
      String label = Strings.isValid(options.getSearchString()) ? options.getSearchString() : "";
      String viewOrApp = "";
      if (options.getApplic().isValid()) {
         viewOrApp = "Applicability = \"" + options.getApplic().toString() + "\"";
      }
      if (options.getView().isValid()) {
         viewOrApp = "View = " + ArtifactQuery.getArtifactFromId(options.getView(), branch).getName();
      }
      if (label.isEmpty()) {
         label = viewOrApp;
      } else {
         if (!viewOrApp.isEmpty()) {
            label = viewOrApp + " - " + label;
         }
      }

      return String.format("%s%s", label,
         options.getIncludeDeleted().areDeletedAllowed() ? " - Options:[Include Deleted]" : "");
   }
}
