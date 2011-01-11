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

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Donald G. Dunne
 * @author Karol M Wilk
 */
public class XWidgetsExampleBlam extends AbstractBlam {

   @Override
   public String getName() {
      return "XWidgets Example";
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      println("Nothing to do here, this is only an example BLAM");
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Util");
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets>" + //
      "<XWidget xwidgetType=\"XLabel\" displayName=\"XLabel\" />" + //
      "<XWidget xwidgetType=\"XCheckBox\" horizontalLabel=\"true\" labelAfter=\"false\" displayName=\"XCheckBox\" />" + //
      "<XWidget xwidgetType=\"XText\" displayName=\"XText\" />" + //
      "<XWidget xwidgetType=\"XBranchSelectWidget\" horizontalLabel=\"true\"  displayName=\"Branch\" />" + //
      "<XWidget xwidgetType=\"XArtifactTypeComboViewer\"  horizontalLabel=\"true\" displayName=\"XArtifactTypeComboViewer\" />" + //
      "<XWidget xwidgetType=\"XArtifactTypeMultiChoiceSelect\" horizontalLabel=\"true\"  displayName=\"XArtifactTypeMultiChoiceSelect\" />" + //
      "<XWidget xwidgetType=\"XAttributeTypeComboViewer\"  horizontalLabel=\"true\" displayName=\"XAttributeTypeComboViewer\" />" + //
      "<XWidget xwidgetType=\"XAttributeTypeMultiChoiceSelect\"  horizontalLabel=\"true\" displayName=\"XAttributeTypeMultiChoiceSelect\" />" + //
      "</xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "This BLAM provides an example of all available XWidgets for use by developers of BLAMs and other UIs";
   }
}