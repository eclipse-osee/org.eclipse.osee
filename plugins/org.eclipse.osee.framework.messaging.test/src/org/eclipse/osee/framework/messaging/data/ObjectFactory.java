/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.framework.messaging.data;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * org.eclipse.osee.framework.messaging.test.msg package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 * 
 * @author Roberto E. Escobar
 */
@XmlRegistry
public class ObjectFactory {

   private final static QName _TestMessage_QNAME = new QName("", "TestMessage");

   /**
    * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
    * org.eclipse.osee.framework.messaging.test.msg
    */
   public ObjectFactory() {
   }

   /**
    * Create an instance of {@link TestMessage }
    */
   public TestMessage createTestMessage() {
      return new TestMessage();
   }

   /**
    * Create an instance of {@link JAXBElement }{@code <}{@link TestMessage }{@code >}
    */
   @XmlElementDecl(namespace = "", name = "TestMessage")
   public JAXBElement<TestMessage> createTestMessage(TestMessage value) {
      return new JAXBElement<>(_TestMessage_QNAME, TestMessage.class, null, value);
   }

}
