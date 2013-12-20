/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.mocks;

import org.eclipse.osee.display.api.components.ArtifactHeaderComponent;
import org.eclipse.osee.display.api.data.ViewArtifact;

/**
 * @author John R. Misinco
 */
public class MockArtifactHeaderComponent implements ArtifactHeaderComponent {

   private ViewArtifact artifact;
   private String errorMessage;

   public ViewArtifact getArtifact() {
      return artifact;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   @Override
   public void setErrorMessage(String shortMsg, String longMsg, MsgType msgType) {
      errorMessage = shortMsg;
   }

   @Override
   public void clearAll() {
      // do nothing
   }

   @Override
   public void setArtifact(ViewArtifact artifact) {
      this.artifact = artifact;
   }
}
