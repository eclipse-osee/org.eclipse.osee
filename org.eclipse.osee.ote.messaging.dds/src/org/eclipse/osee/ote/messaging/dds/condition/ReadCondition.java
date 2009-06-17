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

import java.util.Collection;

import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;

/**
 * This class is here for future functionality that is described in the DDS specification
 * but has not been implemented or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class ReadCondition {
   private DataReader dataReader;
   private Collection<?> sampleStateKinds;
   private Collection<?> viewStateKinds;
   private Collection<?> instanceStateKinds;

   /**
    * @param dataReader
    * @param sampleStateKinds
    * @param viewStateKinds
    * @param instanceStateKinds
    */
   public ReadCondition(DataReader dataReader, Collection<?> sampleStateKinds, Collection<?> viewStateKinds, Collection<?> instanceStateKinds) {
      super();
      this.dataReader = dataReader;
      this.sampleStateKinds = sampleStateKinds;
      this.viewStateKinds = viewStateKinds;
      this.instanceStateKinds = instanceStateKinds;
      
      // This class, and the use of it has not been implemented
      throw new NotImplementedException();
   }

   /**
    * @return Returns the dataReader.
    */
   public DataReader getDataReader() {
      return dataReader;
   }

   /**
    * @return Returns the instanceStateKinds.
    */
   public Collection<?> getInstanceStateKinds() {
      return instanceStateKinds;
   }

   /**
    * @return Returns the sampleStateKinds.
    */
   public Collection<?> getSampleStateKinds() {
      return sampleStateKinds;
   }

   /**
    * @return Returns the viewStateKinds.
    */
   public Collection<?> getViewStateKinds() {
      return viewStateKinds;
   }
}
