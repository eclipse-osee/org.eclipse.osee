/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.messaging.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXB;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;

/**
 * @author Andrew M. Finkbeiner
 */
public class JAXBUtil {

   public static void marshal(Object obj, OutputStream stream) {
      JAXB.marshal(obj, stream);
   }

   public static String marshal(Object obj) throws UnsupportedEncodingException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      marshal(obj, os);
      return new String(os.toByteArray(), "UTF-8");
   }

   public static Object unmarshal(String str, Class<?> clazz) throws UnsupportedEncodingException {
      ByteArrayInputStream is = new ByteArrayInputStream(str.getBytes("UTF-8"));
      return JAXB.unmarshal(new StreamSource(is), clazz);
   }

   public static Object unmarshal(Object body, OseeMessagingListener listener) throws UnsupportedEncodingException {
      Class<?> pojoType = listener.getClazz();
      Object messageBody;
      if (pojoType == null) {
         messageBody = body;
      } else {
         messageBody = JAXBUtil.unmarshal(body.toString(), pojoType);
      }
      return messageBody;
   }

}
