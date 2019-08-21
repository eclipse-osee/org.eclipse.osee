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
   ColorTeam("/ats/attr/" + AtsAttributeTypes.ColorTeam.getIdString()),
   Assignee("/ats/attr/Assignee"),
   IPT("/ats/attr/" + AtsAttributeTypes.IPT.getIdString()),
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
