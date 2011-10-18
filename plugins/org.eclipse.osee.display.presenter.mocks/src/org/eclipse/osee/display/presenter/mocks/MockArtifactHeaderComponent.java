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
import org.eclipse.osee.display.api.data.WebArtifact;

/**
 * @author John Misinco
 */
public class MockArtifactHeaderComponent implements ArtifactHeaderComponent {

   private WebArtifact artifact;
   private String errorMessage;

   public WebArtifact getArtifact() {
      return artifact;
   }

   public String getErrorMessage() {
      return errorMessage;
   }

   @Override
   public void setErrorMessage(String message) {
      errorMessage = message;
   }

   @Override
   public void clearAll() {
   }

   @Override
   public void setArtifact(WebArtifact artifact) {
      this.artifact = artifact;
   }
}
