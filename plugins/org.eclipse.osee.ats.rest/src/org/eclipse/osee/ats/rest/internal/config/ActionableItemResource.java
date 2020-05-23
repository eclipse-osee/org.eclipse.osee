/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.rest.internal.config;

import javax.ws.rs.Path;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.rest.util.AbstractConfigResource;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Donald G. Dunne
 */
@Path("ai")
public class ActionableItemResource extends AbstractConfigResource {
   public ActionableItemResource(AtsApi atsApi, OrcsApi orcsApi) {
      super(AtsArtifactTypes.ActionableItem, atsApi, orcsApi);
   }
}
