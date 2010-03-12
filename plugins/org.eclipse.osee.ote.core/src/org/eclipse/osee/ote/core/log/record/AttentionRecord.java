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
 * @author Charles Shaw
 */
public class AttentionRecord extends TestRecord {

   /**
	 * 
	 */
	private static final long serialVersionUID = -6974833259038544176L;

/**
    * AttentionRecord Constructor. Sets up generic message log message.
    * 
    * @param source The object requesting the logging.
    * @param msg The log message.
    * @param timeStamp <b>True</b> if a timestamp should be recorded, <b>False</b> if not.
    */
   public AttentionRecord(ITestEnvironmentAccessor source, String msg, boolean timeStamp) {
      super(source, TestLevel.ATTENTION, msg, timeStamp);
   }

   /**
    * MessagingRecord Constructor. Sets up generic message log message.
    * 
    * @param source The object requesting the logging.
    * @param msg The log message.
    */
   public AttentionRecord(ITestEnvironmentAccessor source, String msg) {
      this(source, msg, true);
   }
}