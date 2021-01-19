/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.api.workflow;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;

/**
 * NOTE: Items added to this list should be handled in AtsAttributeEndpointImpl.getValidValues()
 *
 * @author Donald G. Dunne
 */
public enum AttributeKey {
   Title("N/A"),
   Priority("/ats/attr/" + AtsAttributeTypes.Priority.getIdString()),
   Assignee("/ats/attr/Assignee"),
   Originator("/ats/attr/Originator"),
   Version("/ats/action/{id}/UnrelasedVersions"),
   assocArt("/ats/action/{id}/assocArt/{attrTypeId}"),
   State("/ats/action/{id}/TransitionToStates");

   private final String url;

   private AttributeKey(String url) {
      this.url = url;
   }

   public String getUrl() {
      return url;
   }
}
