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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;

/**
 * We need to create an SRSRenderer that provides the correct templates. Then we'll use it instead of the WordTemplate
 * processor like we do currently.
 * 
 * @author Robert A. Fisher
 */

public class PublishSrs extends AbstractBlam {

   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      Boolean updateParagraphNumber = variableMap.getBoolean("Update Paragraph Numbers");
      WordTemplateRenderer srsRenderer = new WordTemplateRenderer(WordTemplateRenderer.RENDERER_EXTENSION);
      srsRenderer.setOptions(new VariableMap(WordTemplateRenderer.UPDATE_PARAGRAPH_NUMBER_OPTION, updateParagraphNumber));
      srsRenderer.publishSRS(variableMap);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"true\" displayName=\"Update Paragraph Numbers\" /><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" /></xWidgets>";
   }
}