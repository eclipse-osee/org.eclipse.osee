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
package org.eclipse.osee.ote.messaging.dds.listener;

import org.eclipse.osee.ote.messaging.dds.entity.Subscriber;

/**
 * The listener interface for receiving notification that data is available to
 * <code>DataReader</code> objects in a <code>Subscriber</code>.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.entity.Subscriber
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public interface SubscriberListener extends DataReaderListener{
   
   /**
    * This is the first listener invoked when data becomes available. A
    * reference to the <code>Subscriber</code> with available information
    * is passed to the method.
    */
   public void onDataOnReaders(Subscriber theSubscriber);
}
