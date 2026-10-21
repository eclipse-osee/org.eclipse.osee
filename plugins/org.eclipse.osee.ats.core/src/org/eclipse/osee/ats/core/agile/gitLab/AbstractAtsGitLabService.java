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
package org.eclipse.osee.ats.core.agile.gitLab;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.gitLab.AtsGitLabService;

public abstract class AbstractAtsGitLabService implements AtsGitLabService {

   protected final AtsApi atsApi;

   public AbstractAtsGitLabService(AtsApi atsApi) {
      this.atsApi = atsApi;
   }

   //TODO
}