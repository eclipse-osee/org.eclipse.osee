/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef.builder;

import org.eclipse.osee.ats.api.workdef.IAtsLayoutItem;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.eclipse.osee.ats.api.workdef.model.HeaderDefinition;

/**
 * @author Donald G. Dunne
 */
public class HeaderDefinitionBuilder {

   private final HeaderDefinition headerDefinition;

   public HeaderDefinitionBuilder(IAtsWorkDefinition workDefinition) {
      headerDefinition = new HeaderDefinition(workDefinition);
   }

   public HeaderDefinitionBuilder andLayout(IAtsLayoutItem... items) {
      for (IAtsLayoutItem item : items) {
         headerDefinition.getLayoutItems().add(item);
      }
      return this;
   }

   public HeaderDefinitionBuilder isShowMetricsHeader() {
      headerDefinition.setShowMetricsHeader(true);
      return this;
   }

   public HeaderDefinitionBuilder isShowMetricsHeader(boolean show) {
      headerDefinition.setShowMetricsHeader(show);
      return this;
   }

   public HeaderDefinitionBuilder isShowWorkPackageHeader() {
      headerDefinition.setShowWorkPackageHeader(true);
      return this;
   }

   public HeaderDefinitionBuilder isShowWorkPackageHeader(boolean show) {
      headerDefinition.setShowWorkPackageHeader(show);
      return this;
   }

   public HeaderDefinition getHeaderDefinition() {
      return headerDefinition;
   }

   public HeaderDefinitionBuilder isShowSiblingLinks(boolean show) {
      headerDefinition.setShowSiblingLinks(show);
      return this;
   }

}
