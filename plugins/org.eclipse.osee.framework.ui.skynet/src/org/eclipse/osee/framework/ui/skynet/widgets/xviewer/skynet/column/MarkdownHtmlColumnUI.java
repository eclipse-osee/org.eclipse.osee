/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import java.util.Collection;
import java.util.Map;
import org.eclipse.nebula.widgets.xviewer.IXViewerPreComputedColumn;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;

/**
 * @author Donald G. Dunne
 */
public class MarkdownHtmlColumnUI extends XViewerColumn implements IXViewerPreComputedColumn {

   public static MarkdownHtmlColumnUI instance = new MarkdownHtmlColumnUI();

   public static MarkdownHtmlColumnUI getInstance() {
      return instance;
   }

   private MarkdownHtmlColumnUI() {
      super("framework.markdown.html", "Markdown Html", 80, XViewerAlign.Left, false, SortDataType.String, false,
         "Rendered HTML from Markdown attribute if available.");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public MarkdownHtmlColumnUI copy() {
      MarkdownHtmlColumnUI newXCol = new MarkdownHtmlColumnUI();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public void populateCachedValues(Collection<?> objects, Map<Long, String> preComputedValueMap) {
      for (Object element : objects) {
         if (element instanceof Artifact) {
            Artifact art = (Artifact) element;
            String html = getHtml(art);
            preComputedValueMap.put(getKey(art), html);
         }
      }
   }

   public String getHtml(Artifact art) {
      String md = art.getSoleAttributeValue(CoreAttributeTypes.MarkdownContent, "");
      String html = "";
      if (Strings.isValid(md)) {
         try {
            html = PublishingRequestHandler.convertMarkdownToHtml(md);
         } catch (Exception ex) {
            html = "Error rendering markdown";
         }
      }
      return html;
   }

   private Artifact getArtifactFromElement(Object element) {
      Artifact toReturn = null;
      if (element instanceof Artifact) {
         toReturn = (Artifact) element;
      }
      return toReturn;
   }

   @Override
   public Long getKey(Object obj) {
      Long id = -1L;
      Artifact art = getArtifactFromElement(obj);
      if (art != null) {
         id = art.getId();
      }
      return id;
   }

   @Override
   public String getText(Object obj, Long key, String cachedValue) {
      return cachedValue;
   }

   /**
    * Don't manipulate html so it gets rendered correctly in View Table Report
    */
   @Override
   public String getTreeViewHtml(String str) {
      return str;
   }

}
