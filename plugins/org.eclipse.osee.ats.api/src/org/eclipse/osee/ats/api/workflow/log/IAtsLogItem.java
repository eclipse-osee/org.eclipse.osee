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

/**
 * @author Donald G. Dunne
 */
public interface IAtsLogItem {

   Date getDate();

   String getDate(String pattern);

   void setDate(Date date);

   String getUserId();

   String setUserId(String userId);

   String getMsg();

   void setMsg(String msg);

   LogType getType();

   void setType(LogType type);

   String getState();

   void setState(String state);

}
