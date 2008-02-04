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

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.ARTIFACT_TABLE;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Robert A. Fisher
 */
public class FromArtifactsSearch implements ISearchPrimitive {
   private static final String tables = ARTIFACT_TABLE.toString();
   private static final String FROM_ARTIFACT_ELEMENT = "FromArtifact";

   private List<ISearchPrimitive> criteria;
   private boolean all;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.jdk.core.artifact.search.ISearchPrimitive#getArtIdColName()
    */
   public String getArtIdColName() {
      return "art_id";
   }

   /**
    * 
    */
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

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.search.ISearchPrimitive#getSql()
    */
   public String getCriteriaSql(List<Object> dataList, Branch branch) throws SQLException {
      return "art_id in (" + ArtifactPersistenceManager.getIdSql(criteria, all, dataList, branch) + ")";
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   public String getStorageString() {
      Document document;
      try {
         document = Jaxp.newDocument();
      } catch (ParserConfigurationException ex) {
         throw new IllegalStateException(ex);
      }

      Element root = document.createElement("FromArtifactAttribute");
      root.appendChild(getStorageElements(document));

      return Jaxp.getDocumentXml(document);
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

   public static FromArtifactsSearch getPrimitive(String storageString) {
      try {
         Document document = Jaxp.readXmlDocument(storageString);
         return getPrimitive(Jaxp.getChild(document.getDocumentElement(), FROM_ARTIFACT_ELEMENT));
      } catch (Exception ex) {
         throw new IllegalStateException(ex);
      }
   }

   private static FromArtifactsSearch getPrimitive(Element element) {
      List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      boolean matchAll = Boolean.parseBoolean(element.getAttribute("all"));

      NodeList children = element.getElementsByTagName("*");
      for (int i = 0; i < children.getLength(); i++) {
         Element child = (Element) children.item(i);
         if (child.getNodeName().equals(FROM_ARTIFACT_ELEMENT)) {
            criteria.add(getPrimitive(child));
         } else if (child.getNodeName().equals("Simple")) {
            try {
               Class<?> searchClass = Class.forName(child.getAttribute("class"));
               Method getPrimitiveMethod = searchClass.getMethod("getPrimitive", new Class[] {String.class});
               criteria.add((ISearchPrimitive) getPrimitiveMethod.invoke(null,
                     new Object[] {child.getAttribute("value")}));
            } catch (Exception ex) {
               throw new IllegalStateException(ex);
            }
         } else {
            throw new IllegalStateException("This code should never be reached");
         }
      }

      FromArtifactsSearch fromArtifactSearch = new FromArtifactsSearch(criteria, matchAll);
      return fromArtifactSearch;
   }

   public String toString() {
      StringBuilder sb = new StringBuilder();

      sb.append("(");

      for (ISearchPrimitive primitive : criteria)
         sb.append(primitive);

      sb.append(")");

      return sb.toString();
   }
}
