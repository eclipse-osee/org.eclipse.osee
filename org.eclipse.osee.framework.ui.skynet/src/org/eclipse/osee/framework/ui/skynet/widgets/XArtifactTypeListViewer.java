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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Jeff C. Phillips
 */
public class XArtifactTypeListViewer extends XTypeListViewer {
   private static final ConfigurationPersistenceManager configurationManager =
         ConfigurationPersistenceManager.getInstance();
   private static final String NAME = "XArtifactTypeListViewer";

   public XArtifactTypeListViewer(String keyedBranchName, String defaultValue) {
      super(NAME);

      setContentProvider(new DefaultBranchContentProvider(new ArtifactTypeContentProvider()));
      ArrayList<Object> input = new ArrayList<Object>(1);
      input.add(resolveBranch(keyedBranchName));

      setInput(input);

      try {
         ArtifactSubtypeDescriptor artifactType = configurationManager.getArtifactSubtypeDescriptor(defaultValue);
         setDefaultSelected(artifactType);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }
}