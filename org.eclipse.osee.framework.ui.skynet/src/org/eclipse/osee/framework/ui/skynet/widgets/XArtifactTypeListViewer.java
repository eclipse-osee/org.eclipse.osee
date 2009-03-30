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
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

/**
 * @author Jeff C. Phillips
 */
public class XArtifactTypeListViewer extends XTypeListViewer {
   private static final String NAME = "XArtifactTypeListViewer";

   public XArtifactTypeListViewer(String keyedBranchName, String defaultValue) {
      super(NAME);

      setContentProvider(new DefaultBranchContentProvider(new ArtifactTypeContentProvider(), resolveBranch(keyedBranchName)));
      ArrayList<Object> input = new ArrayList<Object>(1);
      input.add(resolveBranch(keyedBranchName));

      setInput(input);

      if (defaultValue != null) {
         try {
            ArtifactType artifactType = ArtifactTypeManager.getType(defaultValue);
            setDefaultSelected(artifactType);
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }
}