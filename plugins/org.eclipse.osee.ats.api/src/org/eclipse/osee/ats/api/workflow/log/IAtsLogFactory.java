/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.log;

import java.util.Date;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;

/**
 * @author Donald G. Dunne
 */
public interface IAtsLogFactory {

   IAtsLogItem newLogItem(LogType type, Date date, IAtsUser user, String state, String msg) ;

   IAtsLog getLogLoaded(IAtsWorkItem workItem, IAttributeResolver attrResolver);

   void writeToStore(IAtsWorkItem workItem, IAttributeResolver attrResolver, IAtsChangeSet changes);

   IAtsLog getLog();

   ILogStorageProvider getLogProvider(IAtsWorkItem workItem, IAttributeResolver attrResolver);

}
