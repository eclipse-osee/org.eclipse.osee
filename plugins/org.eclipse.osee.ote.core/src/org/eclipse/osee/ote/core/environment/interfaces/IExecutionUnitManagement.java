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
package org.eclipse.osee.ote.core.environment.interfaces;

import org.eclipse.osee.ote.core.TestException;


/**
 * This interface provides the basic abilities that must be able to 
 * be performed on any ExecutionUnit being run by the system. All 
 * classes which provide control over an ExecutionUnit should implement
 * at least this interface so the environments can make use of them.
 * 
 * @author Robert A. Fisher
 */
public interface IExecutionUnitManagement {
   public void startExecutionUnit() throws Exception;
   public void setupExecutionUnit(Object execUnitConfig) throws Exception;
   public void runPrimaryOneCycle() throws InterruptedException, TestException;
   public void stopExecutionUnit() throws InterruptedException;
   public void init() throws Exception;
   /**
    * 
    */
   public void dispose();
}
