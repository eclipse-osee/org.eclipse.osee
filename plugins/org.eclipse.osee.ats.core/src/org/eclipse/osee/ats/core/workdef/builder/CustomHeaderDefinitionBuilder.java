/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.workdef.builder;

import org.eclipse.osee.ats.api.workdef.model.HeaderDefinition;
import org.eclipse.osee.ats.api.workdef.model.LayoutItem;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;

/**
 * @author Donald G. Dunne
 */
public class CustomHeaderDefinitionBuilder {

   private final HeaderDefinition headerDefinition;

   public CustomHeaderDefinitionBuilder(WorkDefinition workDefinition) {
      headerDefinition = new HeaderDefinition(workDefinition);
   }

   public CustomHeaderDefinitionBuilder andLayout(LayoutItem... items) {
      for (LayoutItem item : items) {
         headerDefinition.getLayoutItems().add(item);
      }
      return this;
   }

   public CustomHeaderDefinitionBuilder isShowMetricsHeader() {
      headerDefinition.setShowMetricsHeader(true);
      return this;
   }

   public CustomHeaderDefinitionBuilder showMetricsHeader(boolean show) {
      headerDefinition.setShowMetricsHeader(show);
      return this;
   }

   public HeaderDefinition getHeaderDefinition() {
      return headerDefinition;
   }

   public CustomHeaderDefinitionBuilder isShowSiblingLinks(boolean show) {
      headerDefinition.setShowSiblingLinks(show);
      return this;
   }

}
