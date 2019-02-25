/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.api;

import org.eclipse.osee.activity.api.ActivityLog;

/**
 * @author Angel Avila
 * @author David W. Miller
 */
public interface DefineApi {

   //Traceability
   TraceabilityOperations getTraceabilityOperations();

   //MS Word
   MSWordOperations getMSWordOperations();

   //Data Rights
   DataRightsOperations getDataRightsOperations();

   //Import
   ImportOperations getImportOperations();

   ActivityLog getActivityLog();

}