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
package org.eclipse.osee.ote.core.log.record;

import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.log.TestLevel;

/**
 * @author Ryan D. Brooks
 * @author Andrew M. Finkbeiner
 */
public class DebugRecord extends TestRecord {

   /**
	 * 
	 */
	private static final long serialVersionUID = -5340996017961551683L;

/**
    * DebugRecord Constructor. Invokes TestRecord with the log message source, the log message, and the ability to include a timestamp.
    * 
    * @param source The object requesting the logging.
    * @param msg The log message.
    * @param timeStamp <b>True </b> if time stamp should be logged, <b>False </b> if not.
    */
   public DebugRecord(ITestEnvironmentAccessor source, String msg, boolean timeStamp) {
      super(source, TestLevel.DEBUG, msg, timeStamp);
   }

   /**
    * DebugRecord Constructor. Invokes TestRecord with the log message source and the log message.
    * 
    * @param source The object requesting the logging.
    * @param msg The log message.
    */
   public DebugRecord(ITestEnvironmentAccessor source, String msg) {
      this(source, msg, true);
   }
}