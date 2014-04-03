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
package org.eclipse.osee.disposition.rest.internal.importer;

import org.xml.sax.SAXException;

/**
 * @author Angel Avila
 */
public class BreakSaxException extends SAXException {

   public BreakSaxException(String string) {
      super(string);
   }
   /**
    * 
    */
   private static final long serialVersionUID = 220103479218259961L;

}
