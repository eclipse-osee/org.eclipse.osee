/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author Donald G. Dunne
 */
public class JsonFactory {

   public static org.codehaus.jackson.JsonFactory create() {
      ObjectMapper mapper = new ObjectMapper();
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm a z");
      mapper.setDateFormat(df);
      org.codehaus.jackson.JsonFactory jsonFactory = mapper.getJsonFactory();
      return jsonFactory;
   }
}
