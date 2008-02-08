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

import java.io.FileInputStream;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.io.Streams;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;

/**
 * @author Robert A. Fisher
 */
public class PublishSrs implements BlamOperation {

   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      String masterTemplate =
            new String(Streams.getByteArray(new FileInputStream(variableMap.getString("Master Template"))), "UTF-8");
      String slaveTemplate =
            new String(Streams.getByteArray(new FileInputStream(variableMap.getString("Slave Template"))), "UTF-8");

      WordTemplateProcessor processor = new WordTemplateProcessor(masterTemplate, slaveTemplate);

      variableMap.setValue("MasterFileName", "SRS");
      processor.applyTemplate(FileSystemRenderer.ensureRenderFolderExists(PresentationType.PREVIEW), variableMap);
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Master Template\" /><XWidget xwidgetType=\"XText\" displayName=\"Slave Template\" /><XWidget xwidgetType=\"XBranchListViewer\" displayName=\"Branch\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "Select parameters below and click the play button at the top right.";
   }
}