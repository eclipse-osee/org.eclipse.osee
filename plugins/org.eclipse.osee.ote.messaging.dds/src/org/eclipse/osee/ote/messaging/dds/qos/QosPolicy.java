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
package org.eclipse.osee.ote.messaging.dds.qos;

import org.eclipse.osee.ote.messaging.dds.NotImplementedException;

/**
 * This class is here for future functionality that is described in the DDS specification
 * but has not been implemented or used.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class QosPolicy {
   public final static QosPolicy USERDATA_QOS_POLICY = new QosPolicy("UserData", 1);
   
   private String policyName;
   private long policyId;
   

   /**
    * @param policyName
    * @param policyId
    */
   private QosPolicy(String policyName, long policyId) {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      this.policyName = policyName;
      this.policyId = policyId;
   }

   public String getPolicyName() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return policyName;
   }
   
   public long getPolicyId() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return policyId;
   }
}
