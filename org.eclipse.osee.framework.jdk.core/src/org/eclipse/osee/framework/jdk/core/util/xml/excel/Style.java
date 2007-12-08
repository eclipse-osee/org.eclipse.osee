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
package org.eclipse.osee.framework.jdk.core.util.xml.excel;

import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Ryan D. Brooks
 */
public class Style implements Xmlizable {

   public Element toXml(Document doc) {
      return null;
   }

   public void initializeFromXml(Document doc) {
   }
}
