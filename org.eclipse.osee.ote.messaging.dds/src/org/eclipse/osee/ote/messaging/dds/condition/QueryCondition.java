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
package org.eclipse.osee.ote.messaging.dds.condition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;


/**
 * This class is here for future functionality that is described in the DDS specification
 * but has not been implemented or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class QueryCondition {
   private String queryExpression;
   private Collection<String> queryArguments;

   /**
    * @param queryExpression
    * @param queryArguments
    */
   public QueryCondition(String queryExpression, Collection<String> queryArguments) {
      super();
      this.queryExpression = queryExpression;
      this.queryArguments = Collections.synchronizedList(new ArrayList<String>(queryArguments));
      
      // This class, and the use of it has not been implemented
      throw new NotImplementedException();
   }

   /**
    * @return Returns the queryArguments.
    */
   public Collection<String> getQueryArguments() {
      return queryArguments;
   }

   /**
    * @param queryArguments The queryArguments to set.
    */
   public ReturnCode setQueryArguments(Collection<String> queryArguments) {
      this.queryArguments = Collections.synchronizedList(new ArrayList<String>(queryArguments));
      return ReturnCode.OK;
   }

   /**
    * @return Returns the queryExpression.
    */
   public String getQueryExpression() {
      return queryExpression;
   }
}
