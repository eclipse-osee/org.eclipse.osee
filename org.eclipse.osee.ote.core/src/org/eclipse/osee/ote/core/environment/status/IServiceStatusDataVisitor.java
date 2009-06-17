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
package org.eclipse.osee.ote.core.environment.status;


/**
 * @author Roberto E. Escobar
 */
public interface IServiceStatusDataVisitor {

   public void asCommandAdded(CommandAdded commandAdded);
   
   public void asCommandRemoved(CommandRemoved commandRemoved);
   
   public void asEnvironmentError(EnvironmentError environmentError);
   
   public void asSequentialCommandBegan(SequentialCommandBegan sequentialCommandBegan);
   
   public void asSequentialCommandEnded(SequentialCommandEnded sequentialCommandEnded);
   
   public void asTestPointUpdate(TestPointUpdate testPointUpdate);
   
   
   public void asTestServerCommandComplete(TestServerCommandComplete end);
   
   public void asTestComplete(TestComplete testComplete);

/**
 * @param testStart
 */
public void asTestStart(TestStart testStart);
   
}
