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
import org.eclipse.osee.ats.api.user.IAtsUser;

/**
 * @author Donald G. Dunne
 */
public interface IAtsLogItem {

   public abstract Date getDate();

   public abstract String getDate(String pattern);

   public abstract void setDate(Date date);

   public abstract String getUserId();

   public abstract String getMsg();

   public abstract void setMsg(String msg);

   public abstract IAtsUser getUser();

   public abstract LogType getType();

   public abstract void setType(LogType type);

   public abstract String toHTML(String labelFont);

   public abstract void setUser(IAtsUser user);

   public abstract String getState();

   public abstract void setState(String state);

}