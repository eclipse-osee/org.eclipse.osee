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
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation;
import org.eclipse.osee.framework.ui.skynet.render.FileSystemRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;

/**
 * @author Robert A. Fisher
 */
public class PublishSrs implements BlamOperation {

   @SuppressWarnings("unchecked")
   public void runOperation(BlamVariableMap variableMap, Branch branch, IProgressMonitor monitor) throws Exception {

      // Master Template <XWidget xwidgetType="XText" displayName="masterTemplate" />
      // Slave Template <XWidget xwidgetType="XText" displayName="slaveTemplate" />

      String masterTemplate =
            new String(Streams.getByteArray(new FileInputStream(variableMap.getString("masterTemplate"))), "UTF-8");
      String slaveTemplate =
            new String(Streams.getByteArray(new FileInputStream(variableMap.getString("slaveTemplate"))), "UTF-8");

      WordTemplateProcessor processor = new WordTemplateProcessor(masterTemplate, slaveTemplate);

      BlamVariableMap blamVariableMap = new BlamVariableMap();
      blamVariableMap.setValue("MasterFileName", "SRS");
      blamVariableMap.setValue("Branch", variableMap.getValue("Branch"));

      processor.applyTemplate(FileSystemRenderer.ensureRenderFolderExists(PresentationType.PREVIEW), blamVariableMap);
   }
}