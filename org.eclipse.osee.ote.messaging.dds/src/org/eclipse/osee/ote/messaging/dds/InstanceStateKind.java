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
package org.eclipse.osee.ote.messaging.dds;

/**
 * The class which stores all of the available states which an instance can
 * be in. The name of any of the values can be acquired from the <code>getKindName()</code>
 * method inherited from <code>Kind</code>.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.Kind
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class InstanceStateKind extends Kind {

   public final static InstanceStateKind ALIVE = new InstanceStateKind("Alive instance state", 1);
   public final static InstanceStateKind NOT_ALIVE_DISPOSED = new InstanceStateKind("Not alive disposed instance state", 2);
   public final static InstanceStateKind NOT_ALIVE_NO_WRITERS = new InstanceStateKind("Not alive no writers instance state", 3);
   
   /**
    * Local constructor for creating <code>InstanceStateKind</code> objects.
    * 
    * @param kindName The name of the kind
    * @param kindId The id value of the kind
    */
   private InstanceStateKind(String kindName, long kindId) {
      super(kindName, kindId);
   }
   
}
