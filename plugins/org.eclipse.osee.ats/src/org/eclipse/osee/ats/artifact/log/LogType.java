package org.eclipse.osee.ats.artifact.log;

import org.eclipse.osee.framework.core.exception.OseeArgumentException;

public enum LogType {
   None,
   Originated,
   StateComplete,
   StateCancelled,
   StateEntered,
   Released,
   Error,
   Assign,
   Note,
   Metrics;

   public static LogType getType(String type) throws OseeArgumentException {
      for (Enum<LogType> e : LogType.values()) {
         if (e.name().equals(type)) {
            return (LogType) e;
         }
      }
      throw new OseeArgumentException("Unhandled LogType: [%s]", type);
   }

};
