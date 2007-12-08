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

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class XTextDescriptiveName extends XText {

   public Artifact artifact;

   public XTextDescriptiveName(String displayLabel) {
      super(displayLabel);
   }

   @Override
   public void set(String text) {
      super.set(text);
   }

   @Override
   public void save() {
      if (isDirty()) {
         artifact.setDescriptiveName(get());
      }
   }

   @Override
   public boolean isDirty() {
      return (!artifact.getDescriptiveName().equals(get()));
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
      super.set(artifact.getDescriptiveName());
   }

}
