/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
