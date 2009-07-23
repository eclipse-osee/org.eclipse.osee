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

import java.nio.ByteBuffer;

/**
 * @author Ken J. Aguilar
 */
public interface IOteData extends Data {
   /**
    * signals the end of processing for this data instance. This will return it to the data cache so that it can be
    * reused
    */
   void finish();

   /**
    * gets the data buffer associated with this instance.
    */
   ByteBuffer getDataBuffer();
}
