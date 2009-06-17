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
package org.eclipse.osee.ote.define.parser.handlers;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class VersionInformationHandler extends AbstractParseHandler {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.ote.ui.define.parser.handlers.AbstractParseHandler#processSaxChunk(org.w3c.dom.Element)
    */
   @Override
   protected void processSaxChunk(Element element) {
      List<Element> versions = Jaxp.getChildDirects(element, "Version");
      for (Element version : versions) {
         notifyOnDataEvent("name", version.getAttribute("name"));
         notifyOnDataEvent("underTest", version.getAttribute("underTest"));
         notifyOnDataEvent("version", version.getAttribute("version"));
         notifyOnDataEvent("versionUnit", version.getAttribute("versionUnit"));
         // item.getChildren().add(new BaseOutfileTreeItem(name, versionTitle,
         // String.format("unitId[%s] underTest[%s]", unit, underTest), null));
      }
   }
}
