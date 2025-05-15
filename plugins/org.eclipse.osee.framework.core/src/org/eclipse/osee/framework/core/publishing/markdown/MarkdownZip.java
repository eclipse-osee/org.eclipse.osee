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

import com.vladsch.flexmark.util.ast.Node;
import java.util.HashMap;
import java.util.HashSet;

public class MarkdownZip {
   private final HashMap<String, String> imageContentMap;
   private final Node markdownDocument;

   public MarkdownZip(HashMap<String, String> imageContentMap, Node markdownDocument) {
      this.imageContentMap = imageContentMap;
      this.markdownDocument = markdownDocument;
   }

   public HashMap<String, String> getImageContentMap() {
      return imageContentMap;
   }

   public HashSet<String> getImageNames() {
      return new HashSet<>(imageContentMap.keySet());
   }

   public Node getMarkdownDocument() {
      return markdownDocument;
   }
}
