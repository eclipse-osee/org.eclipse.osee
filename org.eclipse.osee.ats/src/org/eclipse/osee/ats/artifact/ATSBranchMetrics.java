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
package org.eclipse.osee.ats.artifact;

import org.eclipse.osee.ats.util.AtsBranchManager;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author Donald G. Dunne
 */
public class ATSBranchMetrics {
   private static String XML_TAG = "AtsBranchMetrics";
   private Integer numDeletedArtifacts;
   private Integer numModifiedArtifacts;
   private Integer numNonRelationModifiedArtifacts;
   private final AtsBranchManager branchMgr;

   public ATSBranchMetrics(AtsBranchManager branchMgr) {
      this.branchMgr = branchMgr;
   }

   public void load() throws Exception {
      if (numDeletedArtifacts == null) {
         String xml =
               branchMgr.getSmaMgr().getSma().getSoleAttributeValue(
                     ATSAttributes.BRANCH_METRICS_ATTRIBUTE.getStoreName(), "");
         if (!xml.equals("")) {
            System.err.println("Retrieving cached branch metrics values");
            NodeList nodes = Jaxp.readXmlDocument(xml).getElementsByTagName(XML_TAG);
            if (nodes.getLength() > 0) {
               Element element = (Element) nodes.item(0);
               numDeletedArtifacts = new Integer(element.getAttribute("numDelArts")).intValue();
               numModifiedArtifacts = new Integer(element.getAttribute("numModArts")).intValue();
               numNonRelationModifiedArtifacts = new Integer(element.getAttribute("numNonRelModArts")).intValue();
            }
         } else {
            System.err.println("Calculating branch metrics values");
            numDeletedArtifacts = branchMgr.getArtifactsDeleted().size();
            numModifiedArtifacts = branchMgr.getArtifactsModified(true).size();
            numNonRelationModifiedArtifacts = branchMgr.getArtifactsModified(false).size();
         }
      }
   }

   public boolean isCached() throws Exception {
      return !branchMgr.getSmaMgr().getSma().getSoleAttributeValue(
            ATSAttributes.BRANCH_METRICS_ATTRIBUTE.getStoreName(), "").equals("");
   }

   public void persist() throws Exception {
      if (isCached()) return;
      load();
      Document doc = Jaxp.newDocument();
      Element rootElement = doc.createElement(XML_TAG);
      doc.appendChild(rootElement);
      rootElement.setAttribute("numDelArts", String.valueOf(numDeletedArtifacts));
      rootElement.setAttribute("numModArts", String.valueOf(numModifiedArtifacts));
      rootElement.setAttribute("numNonRelModArts", String.valueOf(numNonRelationModifiedArtifacts));
      branchMgr.getSmaMgr().getSma().setSoleAttributeValue(ATSAttributes.BRANCH_METRICS_ATTRIBUTE.getStoreName(),
            Jaxp.getDocumentXml(doc));
      branchMgr.getSmaMgr().getSma().persistAttributes();
   }

   /**
    * @return the numDeletedArtifacts
    */
   public Integer getNumDeletedArtifacts() throws Exception {
      load();
      return numDeletedArtifacts;
   }

   /**
    * @param numDeletedArtifacts the numDeletedArtifacts to set
    */
   public void setNumDeletedArtifacts(Integer numDeletedArtifacts) {
      this.numDeletedArtifacts = numDeletedArtifacts;
   }

   /**
    * @return the numModifiedArtifacts
    */
   public Integer getNumModifiedArtifacts() throws Exception {
      return numModifiedArtifacts;
   }

   /**
    * @param numModifiedArtifacts the numModifiedArtifacts to set
    */
   public void setNumModifiedArtifacts(Integer numModifiedArtifacts) {
      this.numModifiedArtifacts = numModifiedArtifacts;
   }

   /**
    * @return the numNonRelationModifiedArtifacts
    */
   public Integer getNumNonRelationModifiedArtifacts() throws Exception {
      return numNonRelationModifiedArtifacts;
   }

   /**
    * @param numNonRelationModifiedArtifacts the numNonRelationModifiedArtifacts to set
    */
   public void setNumNonRelationModifiedArtifacts(Integer numNonRelationModifiedArtifacts) {
      this.numNonRelationModifiedArtifacts = numNonRelationModifiedArtifacts;
   }

}