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
package org.eclipse.osee.framework.ui.skynet.blam;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.factory.IArtifactFactory;
import org.eclipse.osee.framework.ui.skynet.XWidgetParser;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayout;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.DynamicXWidgetLayoutData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author Ryan D. Brooks
 */
public class BlamWorkflow extends Artifact {
   public static final String ARTIFACT_NAME = "Blam Workflow";
   private final List<BlamOperation> operations;
   private XWidgetParser xWidgetParser;
   private List<DynamicXWidgetLayoutData> layoutDatas;
   private DynamicXWidgetLayout dynamicXWidgetLayout;

   /**
    * @param parentFactory
    * @param guid
    * @param humanReadableId
    * @param branch
    * @throws SQLException
    */
   public BlamWorkflow(IArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch) throws SQLException {
      super(parentFactory, guid, humanReadableId, branch);

      this.operations = new ArrayList<BlamOperation>();
      this.xWidgetParser = new XWidgetParser();
      this.dynamicXWidgetLayout = new DynamicXWidgetLayout();
      this.layoutDatas = new LinkedList<DynamicXWidgetLayoutData>();
   }

   @Override
   public void onBirth() throws SQLException {
      super.onBirth();
      setSoleAttributeValue("Workflow Definition",
            "<Workflow><Operation name=\"org.my.plugin.blamExtensionId\" /></Workflow>");
   }

   private void loadFromXml() throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException, CoreException {
      String blamXml = getSoleAttributeValue("Workflow Definition");
      Document document = Jaxp.readXmlDocument(blamXml);
      Element rootElement = document.getDocumentElement();

      NodeList operations = rootElement.getElementsByTagName("Operation");
      for (int i = 0; i < operations.getLength(); i++) {
         loadBlamOperationFromXml((Element) operations.item(i));
      }

      NodeList xwidgets = rootElement.getElementsByTagName("Widgets");
      for (int i = 0; i < xwidgets.getLength(); i++) {
         setLayoutData((Element) xwidgets.item(i));
      }
   }

   public void saveLayoutData(String xml) throws ParserConfigurationException, SAXException, IOException, IllegalArgumentException, CoreException, SQLException {
      String blamXml = getSoleAttributeValue("Workflow Definition");
      Document document = Jaxp.readXmlDocument(blamXml);
      Element rootElement = document.getDocumentElement();

      NodeList oldXwidgets = rootElement.getElementsByTagName("Widgets");

      // delete all old Xwidgets
      for (int i = 0; i < oldXwidgets.getLength(); i++) {
         rootElement.removeChild(oldXwidgets.item(i));
      }

      String doc = Jaxp.getDocumentXml(document);
      doc = doc.replace("<Workflow>", "<Workflow>" + xml);

      setSoleAttributeValue("Workflow Definition", doc);
      persist();
   }

   private String getOperationsListing(IExtensionRegistry registry) {
      StringBuilder strB = new StringBuilder(1000);
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.framework.ui.skynet.BlamOperation");
      IExtension[] extensions = point.getExtensions();

      for (IExtension extension : extensions) {
         strB.append("Ext Point Id: ");
         strB.append(extension.getUniqueIdentifier());
         strB.append('\n');
      }
      return strB.toString();
   }

   private void loadBlamOperationFromXml(Element operation) throws CoreException, IllegalArgumentException {
      String operationName = operation.getAttribute("name");
      if (operationName.equals("")) {
         throw new IllegalArgumentException("The operation name must be specified");
      }

      IExtensionRegistry registry = Platform.getExtensionRegistry();
      IExtension extension = registry.getExtension("org.eclipse.osee.framework.ui.skynet.BlamOperation", operationName);

      if (extension == null) {
         throw new IllegalArgumentException(
               "No extension for org.eclipse.osee.framework.ui.skynet.BlamOperation with the name " + operationName + " was found.\n\n" + getOperationsListing(registry));
      }

      IConfigurationElement[] configElements = null;
      configElements = extension.getConfigurationElements();
      for (int j = 0; j < configElements.length; j++) {
         BlamOperation blamOperation = (BlamOperation) configElements[j].createExecutableExtension("className");
         operations.add(blamOperation);
      }
   }

   public List<DynamicXWidgetLayoutData> getLayoutDatas() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, CoreException {
      if (layoutDatas.isEmpty()) {
         getOperations();
      }
      return layoutDatas;
   }

   private void setLayoutData(Element element) throws ParserConfigurationException, SAXException, IOException {
      layoutDatas = xWidgetParser.extractlayoutDatas(dynamicXWidgetLayout, element);
   }

   public List<BlamOperation> getOperations() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, CoreException {
      operations.clear();
      loadFromXml();
      return operations;
   }
}