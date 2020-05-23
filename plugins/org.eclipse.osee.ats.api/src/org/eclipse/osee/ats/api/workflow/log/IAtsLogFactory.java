/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.ats.api.workflow.log;

import java.util.Date;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;

/**
 * @author Donald G. Dunne
 */
public interface IAtsLogFactory {

   IAtsLogItem newLogItem(LogType type, Date date, AtsUser user, String state, String msg);

   IAtsLog getLogLoaded(IAtsWorkItem workItem, IAttributeResolver attrResolver);

   void writeToStore(IAtsWorkItem workItem, IAttributeResolver attrResolver, IAtsChangeSet changes);

   IAtsLog getLog();

   ILogStorageProvider getLogProvider(IAtsWorkItem workItem, IAttributeResolver attrResolver);

}
