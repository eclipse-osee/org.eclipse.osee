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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Robert A. Fisher
 */
public class FromArtifactsSearch implements ISearchPrimitive {
   private static final String FROM_ARTIFACT_ELEMENT = "FromArtifact";

   private final List<ISearchPrimitive> criteria;
   private final boolean all;

   @Override
   public String getArtIdColName() {
      return "art_id";
   }

   public FromArtifactsSearch(List<ISearchPrimitive> criteria, boolean all) {
      this.criteria = criteria;
      this.all = all;
   }

   public FromArtifactsSearch(ISearchPrimitive primitive) {
      this.criteria = new ArrayList<ISearchPrimitive>(1);
      this.criteria.add(primitive);
      // all doesn't matter for just one primitive, so assume true
      this.all = true;
   }

   @Override
   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      return "art_id in (" + ArtifactPersistenceManager.getIdSql(criteria, all, dataList, branch) + ")";
   }

   @Override
   public String getTableSql(List<Object> dataList, Branch branch) {
      return "osee_artifact";
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();

      sb.append("(");

      for (ISearchPrimitive primitive : criteria) {
         sb.append(primitive);
      }

      sb.append(")");

      return sb.toString();
   }

   @Override
   public String getStorageString() {
      try {
         Document document = Jaxp.newDocumentNamespaceAware();

         Element root = document.createElement("FromArtifactAttribute");
         root.appendChild(getStorageElements(document));

         return Jaxp.getDocumentXml(document);
      } catch (Exception ex) {
         throw new IllegalStateException(ex);
      }
   }

   private Element getStorageElements(Document document) {
      Element rootElement = document.createElement(FROM_ARTIFACT_ELEMENT);
      rootElement.setAttribute("all", Boolean.toString(all));
      document.appendChild(rootElement);

      Element child;
      for (ISearchPrimitive primitive : criteria) {
         if (primitive instanceof FromArtifactsSearch) {
            child = ((FromArtifactsSearch) primitive).getStorageElements(document);
         } else {
            child = document.createElement("Simple");
            child.setAttribute("class", primitive.getClass().getCanonicalName());
            child.setAttribute("value", primitive.getStorageString());
         }
         rootElement.appendChild(child);
      }

      return rootElement;
   }
}
