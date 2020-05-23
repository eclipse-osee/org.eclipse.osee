/*********************************************************************
 * Copyright (c) 2010 Boeing
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
