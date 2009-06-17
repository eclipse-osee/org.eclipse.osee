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
 * The class which stores all of the available read states for a sample. The name
 * of any of the values can be acquired from the <code>getKindName()</code>
 * method inherited from <code>Kind</code>.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.Kind
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class SampleStateKind extends Kind {

   public final static SampleStateKind READ = new SampleStateKind("Read sample state", 1);
   public final static SampleStateKind NOT_READ = new SampleStateKind("Not read sample state", 2);
   
   /**
    * Local constructor for creating <code>SampleStateKind</code> objects.
    * 
    * @param kindName The name of the kind
    * @param kindId The id value of the kind
    */
   private SampleStateKind(String kindName, long kindId) {
      super(kindName, kindId);
   }
   
}
