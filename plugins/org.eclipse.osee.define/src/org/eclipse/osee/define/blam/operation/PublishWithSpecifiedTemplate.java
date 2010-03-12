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
package org.eclipse.osee.define.blam.operation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.render.ITemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;

/**
 * @author Jeff C. Phillips
 * @author Theron Virgin
 */
public class PublishWithSpecifiedTemplate extends AbstractBlam {
   private List<Artifact> templates;

   @Override
   public String getName() {
      return "Publish With Specified Template";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      populateTemplateList();
      Boolean updateParagraphNumber = variableMap.getBoolean("Update Paragraph Numbers");
      Artifact master = getTemplate(variableMap.getString("Master Template"));
      Artifact slave = getTemplate(variableMap.getString("Slave Template"));
      Branch branch = variableMap.getBranch("Branch (If Template specifies Artifacts)");
      List<Artifact> artifacts = variableMap.getArtifacts("Artifacts (If Not Specified in Template)");
      if (artifacts != null && !artifacts.isEmpty()) {
         branch = artifacts.get(0).getBranch();
      }
      if (artifacts != null && artifacts.isEmpty()) {
         artifacts = null;
      }
      VariableMap newVariableMap = new VariableMap();
      newVariableMap.setValue("Branch", branch);
      newVariableMap.setValue("Update Paragraph Numbers", updateParagraphNumber);

      WordTemplateRenderer renderer = new WordTemplateRenderer();

      SkynetTransaction transaction = new SkynetTransaction(branch, "BLAM: Publish with specified template");

      renderer.setOptions(new VariableMap(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION, updateParagraphNumber,
            ITemplateRenderer.TRANSACTION_OPTION, transaction));
      renderer.publish(newVariableMap, master, slave, artifacts);

      transaction.execute();
   }

   @Override
   public String getDescriptionUsage() {
      return "Select a Master or Master/Slave template and click the play button at the top right.";
   }

   @Override
   public String getXWidgetsXml() {
      populateTemplateList();
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Update Paragraph Numbers\" />");

      builder.append("<XWidget xwidgetType=\"XLabel\" displayName=\" \" /><XWidget xwidgetType=\"XCombo(");
      for (Artifact art : templates) {
         builder.append(art.getSafeName());
         builder.append(",");
      }
      builder.append(")\" displayName=\"Master Template\" horizontalLabel=\"true\"/>");
      builder.append("<XWidget xwidgetType=\"XCombo(");
      for (Artifact art : templates) {
         builder.append(art.getSafeName());
         builder.append(",");
      }

      builder.append(")\" displayName=\"Slave Template\" horizontalLabel=\"true\"/><XWidget xwidgetType=\"XLabel\" displayName=\" \" />");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch (If Template specifies Artifacts)\" defaultValue=\"" + BranchManager.getLastBranch().getGuid() + "\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Artifacts (If Not Specified in Template)\" />");

      builder.append("</xWidgets>");
      return builder.toString();
   }

   private void populateTemplateList() {
      templates = TemplateManager.getAllTemplates();
      Collections.sort(templates);
   }

   private Artifact getTemplate(String templateName) {
      for (Artifact artifact : templates) {
         if (artifact.getSafeName().equals(templateName)) {
            return artifact;
         }
      }
      return null;
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish");
   }
}
