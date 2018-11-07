/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workflow.log;

import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Donald G. Dunne
 */
public enum LogType {
   None,
   Assign,
   Released,
   Originated,
   StateComplete,
   StateCancelled,
   StateEntered,
   Error,
   Note,
   Metrics;

   public static LogType getType(String type) {
      for (Enum<LogType> e : LogType.values()) {
         if (e.name().equals(type)) {
            return (LogType) e;
         }
      }
      throw new OseeArgumentException("Unhandled LogType: [%s]", type);
   }

};
