/*********************************************************************
 * Copyright (c) 2010 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.messaging.internal;

import jakarta.xml.bind.JAXB;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.xml.transform.stream.StreamSource;
import org.eclipse.osee.framework.jdk.core.util.xml.Xml;
import org.eclipse.osee.framework.messaging.OseeMessagingListener;

/**
 * @author Andrew M. Finkbeiner
 */
public class JAXBUtil {

   public static void marshal(Object obj, OutputStream stream) {
      runWithJaxbClassLoader(() -> JAXB.marshal(obj, stream));
   }

   /**
    * Runs a JAXB operation with the thread context classloader temporarily set to this bundle's
    * classloader.
    * <p>
    * {@code jakarta.xml.bind.JAXB} discovers its runtime implementation
    * ({@code org.glassfish.jaxb.runtime}) via a {@code ServiceLoader} lookup that uses the thread
    * context classloader, and caches the result on first use. In OSGi the marshal can be triggered
    * from arbitrary threads (e.g. the EventAdmin dispatch thread) whose TCCL cannot see the JAXB
    * runtime; if that thread wins the race to the first marshal, discovery fails and the failure is
    * cached for the JVM lifetime. Pinning the TCCL to this bundle (which imports the runtime) makes
    * discovery deterministic regardless of the calling thread.
    */
   private static void runWithJaxbClassLoader(Runnable jaxbOp) {
      Thread current = Thread.currentThread();
      ClassLoader previous = current.getContextClassLoader();
      try {
         current.setContextClassLoader(JAXBUtil.class.getClassLoader());
         jaxbOp.run();
      } finally {
         current.setContextClassLoader(previous);
      }
   }

   public static String marshal(Object obj) throws UnsupportedEncodingException {
      ByteArrayOutputStream os = new ByteArrayOutputStream();
      marshal(obj, os);
      return new String(os.toByteArray(), "UTF-8");
   }

   public static Object unmarshal(String str, Class<?> clazz) throws UnsupportedEncodingException {
      String sanitized = Xml.removeInvalidChars(str);
      ByteArrayInputStream is = new ByteArrayInputStream(sanitized.getBytes("UTF-8"));
      Object[] result = new Object[1];
      runWithJaxbClassLoader(() -> result[0] = JAXB.unmarshal(new StreamSource(is), clazz));
      return result[0];
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
