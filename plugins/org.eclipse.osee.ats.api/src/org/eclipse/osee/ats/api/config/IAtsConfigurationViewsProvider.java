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

package org.eclipse.osee.ats.api.config;

/**
 * Provide AtsConfig.views json string for update into ATS configuration
 * 
 * @author Donald G. Dunne
 */
public interface IAtsConfigurationViewsProvider {

   public String getViewsJson();

}
