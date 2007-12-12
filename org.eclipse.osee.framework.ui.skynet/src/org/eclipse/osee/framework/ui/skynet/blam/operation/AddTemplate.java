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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;

/**
 * @author Ryan D. Brooks
 */
public class AddTemplate implements BlamOperation {

   /**
    * <XWidget xwidgetType="XText" displayName="presentationType" /> PREVIEW <XWidget xwidgetType="XText"
    * displayName="bundleName" /> org.eclipse.osee.framework.ui.skynet <XWidget xwidgetType="XText"
    * displayName="templateName" /> My Template <XWidget xwidgetType="XText" displayName="templatePath" />
    * support/templates/My_Template.xml
    * 
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap,
    *      org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, Branch branch, IProgressMonitor monitor) throws Exception {

      String presentationType = variableMap.getString("presentationType");
      String bundleName = variableMap.getString("bundleName");
      String templateName = variableMap.getString("templateName");
      String templatePath = variableMap.getString("templatePath");

      WordRenderer wordRenderer =
            (WordRenderer) RendererManager.getInstance().getRendererById("org.eclipse.osee.framework.ui.skynet.word");
      wordRenderer.addTemplate(PresentationType.valueOf(presentationType), bundleName, templateName, templatePath,
            branch);
   }
}