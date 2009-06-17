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
package org.eclipse.osee.ote.ui.test.manager.batches.navigate;

import java.net.URI;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * @author Roberto E. Escobar
 */
public class TestBatchData {

   private String name;
   private XNavigateItem itemFolder;
   private URI testBatchFile;
   private URI projectSetFile;

   protected TestBatchData(XNavigateItem itemFolder, String name, URI projectSetFile, URI testBatchFile) {
      this.name = name;
      this.testBatchFile = testBatchFile;
      this.projectSetFile = projectSetFile;
      this.itemFolder = itemFolder;
   }

   public String getId() {
      return name;
   }

   public URI getTestBatchFile() {
      return testBatchFile;
   }

   public URI getProjectSetFile() {
      return projectSetFile;
   }

   public XNavigateItem getXNavigateItem() {
      return itemFolder;
   }

   public void dispose() {
      XNavigateItem parent = itemFolder.getParent();
      if (parent != null) {
         parent.removeChild(itemFolder);
         itemFolder.setParent(null);
      }
   }
}
