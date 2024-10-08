/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ote.define.parser.handlers;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.ote.define.TestRunField;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class RuntimeVersionsHandler extends AbstractParseHandler {

   @Override
   protected void processSaxChunk(Element element) {
      List<Element> versions = Jaxp.getChildDirects(element, "Version");
      StringBuilder sb = new StringBuilder();
      for (Element el : versions) {
         sb.append(Jaxp.getElementCharacterData(el));
         sb.append("   ");
      }
      notifyOnDataEvent(TestRunField.SYSTEM_OSEE_SERVER_JAR_VERSIONS.toString(), sb.toString());
   }
}
