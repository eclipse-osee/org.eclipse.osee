/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.framework.core.publishing.markdown;

import java.util.HashMap;
import java.util.HashSet;
import org.jsoup.nodes.Document;

public class HtmlZip {
   private final HashMap<String, String> imageContentMap;
   private final Document htmlDocument;

   public HtmlZip(HashMap<String, String> imageContentMap, Document htmlDocument) {
      this.imageContentMap = imageContentMap;
      this.htmlDocument = htmlDocument;
   }

   public HashMap<String, String> getImageContentMap() {
      return imageContentMap;
   }

   public HashSet<String> getImageNames() {
      return new HashSet<>(imageContentMap.keySet());
   }

   public Document getHtmlDocument() {
      return htmlDocument;
   }
}
