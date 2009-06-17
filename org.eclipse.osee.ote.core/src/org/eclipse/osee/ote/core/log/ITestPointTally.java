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
package org.eclipse.osee.ote.core.log;


/**
 * @author John Butler
 */
public interface ITestPointTally {

	   /** 
	    * Resets the test point pass and fail counts to zero.
	    */
	   public void reset();
	   
	   /**
	    * Records test point result.
	    * 
	    * @param pass test point result.
	    *           <b>True</b> for passing.
	    *           <b>False</b> for failing.
	    * 
	    * @return The total number of test points recorded. 
	    */
	   public int tallyTestPoint(boolean pass);
	   
	   /**
	    * @return The total number of test points recorded.
	    */
	   public int getTestPointTotal();
}