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
package org.eclipse.osee.ats.workflow.vue;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Donald G. Dunne
 */
public class VueDiagram {

   private final ArrayList<VueLink> links = new ArrayList<VueLink>();
   private final ArrayList<VueNode> vuePages = new ArrayList<VueNode>();
   private final Diagram workflow;
   private static Pattern childPattern =
         Pattern.compile("<child(.*?)>(.*?)</child>", Pattern.DOTALL | Pattern.MULTILINE);

   /**
    * @param vueXml
    */
   public VueDiagram(String workflowId, String vueXml) {
      workflow = new Diagram(workflowId);
      processXml(vueXml);
   }

   private void processXml(String xml) {
      Matcher m = childPattern.matcher(xml);
      while (m.find()) {
         String childParms = m.group(1);
         String matchStr = m.group();
         // System.out.println("Processing child "+childParms);
         if (childParms.contains("xsi:type=\"group\"")) {
            throw new IllegalArgumentException("Can't use grouping in diagram");
         } else if (childParms.contains("xsi:type=\"link\"")) {
            VueLink link = new VueLink(matchStr);
            links.add(link);
         } else if (childParms.contains("xsi:type=\"node\"")) {
            VueNode vuePage = new VueNode(matchStr);
            vuePages.add(vuePage);
            workflow.addPage(vuePage.getWorkPage());
         } else {
            throw new IllegalArgumentException("Unhandled xsi:type");
         }
      }
      for (VueLink link : links) {
         VueNode fromVuePage = getPageFromVueId(link.getFromVueId());
         if (fromVuePage == null) {
            throw new IllegalArgumentException("Can't retrieve fromVuePage with id " + link.getFromVueId());
         }
         VueNode toVuePage = getPageFromVueId(link.getToVueId());
         if (toVuePage == null) {
            throw new IllegalArgumentException(
                  "Can't retrieve toVuePage " + link.getToVueId() + " fromVuePage " + link.getFromVueId() + " named \"" + fromVuePage.getWorkPage().getName() + "\"");
         }
         fromVuePage.getWorkPage().addToPage(toVuePage.getWorkPage(), link.getName().equals("return"));
         toVuePage.getWorkPage().addFromPage(fromVuePage.getWorkPage());
         if (link.isMultiDirectional()) {
            toVuePage.getWorkPage().addToPage(fromVuePage.getWorkPage(), link.getName().equals("return"));
            fromVuePage.getWorkPage().addFromPage(toVuePage.getWorkPage());
         }
         if (link.getName().equals("default")) {
            if (fromVuePage.getWorkPage().getDefaultToPage() == null)
               fromVuePage.getWorkPage().setDefaultToPage(toVuePage.getWorkPage());
            else
               throw new IllegalArgumentException(
                     "Can't have 2 default transitions. Page " + fromVuePage.getWorkPage().getName());
         }
      }
   }

   public VueNode getPageFromVueId(String vueId) {
      for (VueNode page : vuePages)
         if (page.getVueId().equals(vueId)) return page;
      return null;
   }

   /**
    * @return Returns the workflow.
    */
   public Diagram getWorkflow() {
      return workflow;
   }

}
