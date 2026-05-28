/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.search.quick;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.ui.skynet.ArtifactDecorator;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.search.page.ArtifactSearchPage;
import org.eclipse.osee.framework.ui.skynet.search.page.DecoratingArtifactSearchLabelProvider;

/**
 * ATS-specific search result page that uses the ATS label provider to display: &lt;ATS Id&gt; - &lt;Team Def&gt; -
 * &lt;state&gt; - &lt;title&gt;
 *
 * @author Donald G. Dunne
 */
public class AtsSearchResultPage extends ArtifactSearchPage {

   private final ArtifactDecorator atsArtifactDecorator =
      new ArtifactDecorator(Activator.ARTIFACT_SEARCH_RESULTS_ATTRIBUTES_PREF, true);

   @Override
   protected void configureTreeViewer(TreeViewer viewer) {
      super.configureTreeViewer(viewer);
      // Replace label provider with ATS-specific one
      AtsSearchLabelProvider atsLabelProvider = new AtsSearchLabelProvider(this, atsArtifactDecorator);
      viewer.setLabelProvider(new DecoratingArtifactSearchLabelProvider(atsLabelProvider));
   }

   @Override
   protected void configureTableViewer(TableViewer viewer) {
      super.configureTableViewer(viewer);
      // Replace label provider with ATS-specific one
      AtsSearchLabelProvider atsLabelProvider = new AtsSearchLabelProvider(this, atsArtifactDecorator);
      viewer.setLabelProvider(new DecoratingArtifactSearchLabelProvider(atsLabelProvider));
   }
}
