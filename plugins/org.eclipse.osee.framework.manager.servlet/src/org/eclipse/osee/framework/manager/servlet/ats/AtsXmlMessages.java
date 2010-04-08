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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Roberto E. Escobar
 */
public class AtsXmlMessages {

   private final XmlMessage messenger;

   public AtsXmlMessages(XmlMessage messenger) {
      this.messenger = messenger;
   }

   public void sendError(HttpServletResponse response, Throwable ex) {
      messenger.sendError(response, "Ats Error", ex);
   }

   public void sendPrograms(HttpServletResponse response, Collection<Node> nodes) {
      messenger.sendMessage(response, "Ats Programs", "0", nodes);
   }

   public void sendBuilds(HttpServletResponse response, Collection<Node> nodes) {
      messenger.sendMessage(response, "Ats Builds", "0", nodes);
   }

   public void sendWorkflows(HttpServletResponse response, Collection<Node> nodes) {
      for (Node node : nodes) {
         Element changeReportElement = createChangeReportNode(node);
         node.appendChild(changeReportElement);
      }
      messenger.sendMessage(response, "Ats Workflows", "0", nodes);
   }

   public void sendChangeReports(HttpServletResponse response, Collection<Node> nodes) {
      List<Node> nodeList = new ArrayList<Node>();
      for (Node node : nodes) {
         Element changeReportElement = createChangeReportNode(node);
         nodeList.add(changeReportElement);
      }
      messenger.sendMessage(response, "Ats Change Reports", "0", nodeList);
   }

   private Element createChangeReportNode(Node node) {
      String legacyId = Jaxp.getChildText((Element) node, "workflowPcrId");
      String changeReportUrl =
            String.format("%s://changeReports/%s.xml", AtsResourceLocatorProvider.PROTOCOL, legacyId);
      Element changeReportElement = node.getOwnerDocument().createElement("changeReportUrl");
      changeReportElement.setTextContent(changeReportUrl);
      return changeReportElement;
   }
}
