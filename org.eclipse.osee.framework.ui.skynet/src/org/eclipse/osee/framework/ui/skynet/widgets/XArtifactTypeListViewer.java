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
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;

/**
 * @author Jeff C. Phillips
 */
public class XArtifactTypeListViewer extends XTypeListViewer {
   private static final String NAME = "XArtifactTypeListViewer";
   private BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();

   public XArtifactTypeListViewer() {
      super(NAME);

      setContentProvider(new DefaultBranchContentProvider(new ArtifactTypeContentProvider()));
      ArrayList<Object> input = new ArrayList<Object>(1);
      input.add(branchPersistenceManager.getDefaultBranch());

      setInput(input);
   }
}
