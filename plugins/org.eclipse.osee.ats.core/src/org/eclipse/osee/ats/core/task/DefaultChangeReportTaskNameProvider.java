/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.core.task;

import org.eclipse.osee.ats.api.task.create.ChangeReportTaskNameProviderToken;
import org.eclipse.osee.ats.api.task.create.IAtsChangeReportTaskNameProvider;

/**
 * Method to provide task name for given related artifact. Override to provide customized names and set to appropriate
 * crttwd. <br/>
 * <br/>
 * Provided through OSGI-INF
 *
 * @author Donald G. Dunne
 */
public class DefaultChangeReportTaskNameProvider implements IAtsChangeReportTaskNameProvider {

   public DefaultChangeReportTaskNameProvider() {
      // for jax-rs
   }

   @Override
   public ChangeReportTaskNameProviderToken getId() {
      return ChangeReportTaskNameProviderToken.DefaultChangeReportOptionsNameProvider;
   }

}
