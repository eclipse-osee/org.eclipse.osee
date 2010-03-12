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

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.ArtifactLabelProvider;

/**
 * Set a Artifact list
 * 
 * @author Donald G. Dunne
 */
public class XArtifactList extends XListViewer {

   public XArtifactList() throws OseeCoreException {
      this("ArtList", "", "");
   }

   public XArtifactList(String displayLabel) {
      this(displayLabel, "", "");
   }

   public XArtifactList(String displayLabel, String xmlRoot, String xmlSubRoot) {
      super(displayLabel, xmlRoot, xmlSubRoot);
      super.setLabelProvider(new ArtifactLabelProvider());
      super.setContentProvider(new ArrayContentProvider());
   }

}