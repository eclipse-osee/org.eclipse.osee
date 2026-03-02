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

package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.osgi.service.component.annotations.Component;

/**
 * @author Ryan D. Brooks
 */
@Component(service = AbstractBlam.class, immediate = true)
public class PurgeArtifactTypeBlam extends AbstractBlam {
   private boolean convertArtifacts;

   @Override
   public String getName() {
      return "Purge Artifact Type";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Collection<ArtifactTypeToken> purgeArtifactTypes =
         variableMap.getCollection(ArtifactTypeToken.class, "Artifact Type(s) to purge");
      convertArtifacts = variableMap.getBoolean("Convert Artifacts");
      ArtifactTypeToken newArtifactType = convertArtifacts ? variableMap.getArtifactType("New Artifact Type") : null;

      ArtifactTypeManager.purgeArtifactTypesWithCheck(purgeArtifactTypes, newArtifactType);
   }

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andArtifactTypeWidget("Artifact Type(s) to Purge");
      wb.andWidget("Convert Artifacts", WidgetId.XCheckBoxWidget).andLabelAfter().andHorizLabel();
      wb.andArtifactTypeWidget("New Artifact Type");
      return wb.getXWidgetDatas();
   }

   @Override
   public String getDescriptionUsage() {
      return "Purge an artifact type.  Will find artifacts (if any) of this type on all branches and switch their type to the specified type.  Then purge the artifact type ";
   }

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(XNavigateItem.DEFINE_ADMIN, XNavItemCat.OSEE_ADMIN);
   }

}