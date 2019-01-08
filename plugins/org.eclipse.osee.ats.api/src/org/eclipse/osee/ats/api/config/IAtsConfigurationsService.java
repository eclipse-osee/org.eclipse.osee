/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.tx.IAtsConfigTx;
import org.eclipse.osee.ats.api.user.IAtsUser;

/**
 * @author Donald G. Dunne
 */
public interface IAtsConfigurationsService {

   AtsConfigurations getConfigurations();

   AtsConfigurations getConfigurationsWithPend();

   IAtsConfigTx createConfigTx(String string, IAtsUser asUser);

   void setAtsApi(AtsApi atsApi);

}