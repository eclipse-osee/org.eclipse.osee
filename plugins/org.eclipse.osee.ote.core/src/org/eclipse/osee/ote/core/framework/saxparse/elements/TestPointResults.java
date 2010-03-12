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
package org.eclipse.osee.ote.core.framework.saxparse.elements;

import org.eclipse.osee.ote.core.framework.saxparse.ElementHandlers;
import org.xml.sax.Attributes;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class TestPointResults extends ElementHandlers{

   /**
    * @param name
    */
   public TestPointResults() {
      super("TestPointResults");
   }

   @Override
   public Object createStartElementFoundObject(String uri, String localName, String name, Attributes attributes) {
      TestPointResultsData data = new TestPointResultsData(attributes.getValue("aborted"), attributes.getValue("fail"), attributes.getValue("pass"), attributes.getValue("total"));
      return data;
   }
}
