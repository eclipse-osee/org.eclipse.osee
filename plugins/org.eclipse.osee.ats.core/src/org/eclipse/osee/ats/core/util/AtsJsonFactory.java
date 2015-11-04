/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.util;

import java.text.SimpleDateFormat;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Donald G. Dunne
 */
public class AtsJsonFactory {

   private static ObjectMapper mapper;

   public static ObjectMapper getMapper() {
      if (mapper == null) {
         mapper = new ObjectMapper();
         mapper.setDateFormat(new SimpleDateFormat("MMM d, yyyy h:mm:ss aa"));
      }
      return mapper;
   }
}
