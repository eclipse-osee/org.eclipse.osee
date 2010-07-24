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
package org.eclipse.osee.framework.manager.servlet.ats;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collection;
import javax.xml.xpath.XPath;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.resource.management.IResource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Roberto E. Escobar
 */
public final class OperationData {

   public static enum OperationType {
      GET_PROGRAMS,
      GET_BUILDS_BY_PROGRAM_ID,
      GET_WORKFLOWS_BY_IDS,
      GET_WORKFLOWS_BY_PROGRAM_AND_BUILD_ID,
      GET_CHANGE_REPORTS_BY_IDS,
      UNKNOWN;

      public static OperationType fromString(String value) {
         OperationType toReturn = OperationType.UNKNOWN;
         for (OperationType type : OperationType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
               toReturn = type;
               break;
            }
         }
         return toReturn;
      }
   }

   private final PropertyStore data;

   public OperationData(OperationType operationType) {
      this.data = new PropertyStore();
      data.put("operationType", operationType.name());
   }

   public OperationType getOperationType() {
      return OperationType.fromString(data.get("operationType"));
   }

   public String getProgramId() {
      return data.get("programId");
   }

   public String getBuildId() {
      return data.get("buildId");
   }

   public String getItemIds() {
      return data.get("uniqueIds");
   }

   public static Collection<OperationData> fromResource(IResource resource) throws OseeCoreException {
      Collection<OperationData> toReturn = new ArrayList<OperationData>();
      try {
         String data = Lib.inputStreamToString(resource.getContent());
         System.out.println(data);
         Element rootElement = XmlUtil.readXML(new ByteArrayInputStream(data.getBytes()));
         XPath xPath = XmlUtil.createXPath();
         Collection<Node> nodes = XmlUtil.selectNodesViaXPath(xPath, rootElement, "//request");

         for (Node node : nodes) {
            if (node instanceof Element) {
               OperationData operationData = new OperationData(OperationType.UNKNOWN);
               toReturn.add(operationData);

               Element element = (Element) node;
               NodeList list = element.getChildNodes();
               for (int index = 0; index < list.getLength(); index++) {
                  Node childNode = list.item(index);
                  if (childNode instanceof Element) {
                     Element elementNode = (Element) childNode;
                     String name = elementNode.getTagName();
                     String value = Jaxp.getElementCharacterData(elementNode, true);
                     if (Strings.isValid(name) && Strings.isValid(value)) {
                        operationData.data.put(name, value);
                     }
                  }
               }
            }
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
      return toReturn;
   }
}