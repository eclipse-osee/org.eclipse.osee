/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.IHealthStatus;

public class TestException extends RuntimeException {

   /**
    * 
    */
   private static final long serialVersionUID = -5628986844200418864L;

   private final String threadCauseName;
   private final Level level;
   private List<IHealthStatus> status = new ArrayList<IHealthStatus>();

   public TestException(String message, Level level) {
      this(message, level, null);
   }

   public TestException(String message, Level level, Throwable cause) {
      super(message, cause);
      this.level = level;
      threadCauseName = Thread.currentThread().getName();
   }

   /**
    * @param status
    */
   public TestException(String message, List<IHealthStatus> status) {
      this(message, Level.SEVERE);
      this.status = status;
   }

   public String getThreadName() {
      return threadCauseName;
   }

   public Level getLevel() {
      return level;
   }

   public List<IHealthStatus> getHealthStatus() {
      return status;
   }
}
