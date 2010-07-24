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
package org.eclipse.osee.ote.messaging.dds.test.data;

import org.eclipse.osee.ote.messaging.dds.service.Key;
import org.eclipse.osee.ote.messaging.dds.service.TypeSupport;

/**
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class IntMessageTypeSupport extends TypeSupport {

   @Override
   protected int getTypeDataSize() {
      return Integer.SIZE / 8;
   }

   @Override
   protected Key getKey() {
      return null;
   }

   @Override
   protected String getReaderName() {
      return IntMessageReader.class.getCanonicalName();
   }

   @Override
   protected String getWriterName() {
      return IntMessageWriter.class.getCanonicalName();
   }

}
