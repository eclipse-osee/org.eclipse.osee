/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
