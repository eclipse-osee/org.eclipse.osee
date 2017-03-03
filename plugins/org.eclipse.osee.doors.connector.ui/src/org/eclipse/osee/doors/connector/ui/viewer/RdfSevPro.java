/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.doors.connector.ui.viewer;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.doors.connector.core.DoorsArtifact;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author David W. Miller
 */
public class RdfSevPro extends RdfExplorerItem {

   public RdfSevPro(String name, TreeViewer treeViewer, RdfExplorerItem parentItem, RdfExplorer rdfExplorer, DoorsArtifact dwaItem) {
      super(name, treeViewer, parentItem, rdfExplorer, dwaItem);
   }

   @Override
   public Image getImage() {
      return ImageManager.getImage(FrameworkImage.RULE);
   }
}
