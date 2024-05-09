/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

public abstract class AbstractAtsStoreService implements IAtsStoreService {

   protected final AtsApi atsApi;

   public AbstractAtsStoreService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   @Override
   public XResultData validateTypes() {
      WorkflowTypesTestOperation op = new WorkflowTypesTestOperation();
      return op.run();
   }

   @Override
   public Collection<CustomizeData> getCustomizations(String namespace) {
      List<CustomizeData> customizations = new ArrayList<>();
      for (ArtifactId customizationArt : getCustomizeArts()) {
         addCustomizationsFromArts(namespace, customizations, customizationArt);
      }
      return customizations;
   }

   private void addCustomizationsFromArts(String namespace, List<CustomizeData> customizations,
      ArtifactId customizationArt) {
      for (String custXml : atsApi.getAttributeResolver().getAttributesToStringList(customizationArt,
         CoreAttributeTypes.XViewerCustomization)) {
         if (custXml.contains("\"" + namespace + "\"") || custXml.contains("." + namespace + "\"")) {
            CustomizeData data = new CustomizeData(custXml);
            customizations.add(data);
         }
      }
   }

   @Override
   public Collection<CustomizeData> getCustomizationsGlobal(String namespace) {
      List<CustomizeData> customizations = new ArrayList<>();
      for (ArtifactId customizationArt : getGlobalCustomizeArts()) {
         addCustomizationsFromArts(namespace, customizations, customizationArt);
      }
      return customizations;
   }

   private List<ArtifactId> getCustomizeArts() {
      List<ArtifactId> customizationArts = getGlobalCustomizeArts();
      for (ArtifactId artifact : atsApi.getQueryService().getArtifacts(CoreArtifactTypes.User)) {
         customizationArts.add(artifact);
      }
      return customizationArts;
   }

   private List<ArtifactId> getGlobalCustomizeArts() {
      List<ArtifactId> customizationArts = new ArrayList<>();
      for (ArtifactId artifact : atsApi.getQueryService().getArtifacts(CoreArtifactTypes.XViewerGlobalCustomization)) {
         customizationArts.add(artifact);
      }
      return customizationArts;
   }

}
