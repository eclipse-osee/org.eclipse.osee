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
package org.eclipse.osee.ats.workflow;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.WorkflowDiagramFactory;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.AXml;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.w3c.dom.Document;

/**
 * @author Donald G. Dunne
 */
public class VueWorkFlow {

   private ArrayList<VueWorkLink> links = new ArrayList<VueWorkLink>();
   private ArrayList<VueWorkPage> vuePages = new ArrayList<VueWorkPage>();
   private AtsWorkFlow workflow;
   private static String INHERITED_WORKFLOW_TAG = "InheritedWorkflow";
   private static String PAGE_REPLACE_ALL_TAG = "PageIdReplaceAll";
   private static Pattern workPagePattern = Pattern.compile("<WorkPage +pageId=\"(.*?)\".*?\\/>");
   private static Pattern inheritedPattern =
         Pattern.compile("&lt;" + INHERITED_WORKFLOW_TAG + " workflowId=\"(.*?)\"/&gt;",
               Pattern.DOTALL | Pattern.MULTILINE);
   private static Pattern childPattern =
         Pattern.compile("<child(.*?)>(.*?)</child>", Pattern.DOTALL | Pattern.MULTILINE);
   private static Pattern replaceAllPageIdsPattern =
         Pattern.compile("&lt;" + PAGE_REPLACE_ALL_TAG + " search=\"(.*?)\" replace=\"(.*?)\"/&gt;",
               Pattern.DOTALL | Pattern.MULTILINE);
   private static Pattern inheritedDataPattern = Pattern.compile("<notes>(&lt;InheritedWorkflow .*?)</notes>");

   /**
    * @param vueXml
    */
   public VueWorkFlow(String workflowId, String vueXml) {
      workflow = new AtsWorkFlow(workflowId);
      String inheritedVueXml = getInheritedVueXml(workflowId, vueXml);
      if (inheritedVueXml != null) {
         processXml(inheritedVueXml);
         replacePageIds(workflowId, vueXml);
         setOverriddenWorkPageAttributes(vueXml);
         storeInheritData(vueXml);
      } else
         processXml(vueXml);
   }

   private void storeInheritData(String vueXml) {
      Matcher m = inheritedDataPattern.matcher(vueXml);
      if (m.find())
         workflow.setInheritData(AXml.xmlToText(m.group(1)).replaceAll("%nl;", "\r\n"));
      else
         workflow.setInheritData("Unable to extract just inherit block => " + vueXml);
   }

   private void setOverriddenWorkPageAttributes(String xml) {
      String xmlStr = AXml.xmlToText(xml);
      Matcher m = workPagePattern.matcher(xmlStr);
      while (m.find()) {
         try {
            String pageId = m.group(1);
            for (VueWorkPage page : vuePages) {
               if (page.getWorkPage().getId().equals(pageId)) {
                  Document doc = Jaxp.readXmlDocument(m.group());
                  page.getWorkPage().processWorkPageNode(doc);
               }
            }
         } catch (Exception ex) {
            OSEELog.logException(AtsPlugin.class, "Can't process vue xml", ex, true);
         }
      }

   }

   private void replacePageIds(String workflowId, String xml) {
      String srchValue = null;
      String replaceValue = null;
      Matcher m = replaceAllPageIdsPattern.matcher(xml);
      if (!m.find()) {
         if (xml.contains(PAGE_REPLACE_ALL_TAG)) throw new IllegalArgumentException(
               PAGE_REPLACE_ALL_TAG + " tag found, but format is invalid in workflowId " + workflowId);
      }
      srchValue = m.group(1);
      replaceValue = m.group(2);
      for (VueWorkPage page : vuePages) {
         page.getWorkPage().setId(page.getWorkPage().getId().replaceFirst(srchValue, replaceValue));
      }
   }

   private String getInheritedVueXml(String workflowId, String xml) {
      String workflowName = null;
      try {
         Matcher m = inheritedPattern.matcher(xml);
         if (!m.find()) {
            if (xml.contains(INHERITED_WORKFLOW_TAG)) throw new IllegalArgumentException(
                  INHERITED_WORKFLOW_TAG + " tag found, but format is invalid in workflowId " + workflowId);
            return null;
         }
         workflowName = m.group(1);
         NativeArtifact nativeArtifact = WorkflowDiagramFactory.getInstance().getAtsWorkflowArtifact(workflowName);
         InputStream is = nativeArtifact.getNativeContent();
         String vueXml = AFile.readFile(is);
         return vueXml;
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class,
               "Can't load inherited workflow artifact \"" + workflowName + "\"specified in workflowId " + workflowId,
               ex, true);
      }
      return null;
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
            VueWorkLink link = new VueWorkLink(matchStr);
            links.add(link);
         } else if (childParms.contains("xsi:type=\"node\"")) {
            VueWorkPage vuePage = new VueWorkPage(matchStr);
            vuePages.add(vuePage);
            workflow.addPage(vuePage.getWorkPage());
         } else
            throw new IllegalArgumentException("Unhandled xsi:type");
      }
      for (VueWorkLink link : links) {
         VueWorkPage fromVuePage = getPageFromVueId(link.getFromVueId());
         if (fromVuePage == null) throw new IllegalArgumentException("Can't retrieve from page");
         VueWorkPage toVuePage = getPageFromVueId(link.getToVueId());
         if (toVuePage == null) throw new IllegalArgumentException(
               "Can't retrieve to page " + link.getToVueId() + " from page " + link.getFromVueId() + " named \"" + fromVuePage.getWorkPage().getName() + "\"");
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

   public VueWorkPage getPageFromVueId(String vueId) {
      for (VueWorkPage page : vuePages)
         if (page.getVueId().equals(vueId)) return page;
      return null;
   }

   /**
    * @return Returns the workflow.
    */
   public AtsWorkFlow getWorkflow() {
      return workflow;
   }

}
