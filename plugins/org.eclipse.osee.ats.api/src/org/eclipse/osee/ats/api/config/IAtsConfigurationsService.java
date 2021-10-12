/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigurationsService {

   AtsConfigurations getConfigurations();

   /**
    * @return newly retrieved configs. Updates current cache with new.
    */
   AtsConfigurations getConfigurationsWithPend();

   IAtsConfigTx createConfigTx(String string);

   void setAtsApi(AtsApi atsApi);

   XResultData configAtsDatabase(AtsApi atsApi);

   boolean isAtsBaseCreated();

   AtsUser getUserByUserId(String userId);

   AtsUser getUserByName(String name);

   boolean isConfigLoaded();

   AtsUser getUser(ArtifactId userArt);

}