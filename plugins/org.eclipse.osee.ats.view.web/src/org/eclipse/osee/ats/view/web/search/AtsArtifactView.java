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
package org.eclipse.osee.ats.view.web.search;

import org.eclipse.osee.ats.view.web.AtsUiApplication;
import org.eclipse.osee.display.view.web.search.OseeArtifactView;

/**
 * @author Shawn F. Cook
 */
public class AtsArtifactView extends OseeArtifactView {

   private boolean populated = false;
   private String requestedDataId = "";

   @Override
   public void attach() {
      if (!populated) {
         try {
            AtsUiApplication atsApp = (AtsUiApplication) getApplication();
            searchPresenter = atsApp.getAtsWebSearchPresenter();
            searchHeader = atsApp.getAtsSearchHeaderComponent();
            callInitArtifactPage();
            createLayout();
         } catch (Exception e) {
            System.out.println("OseeArtifactNameLinkComponent.attach - CRITICAL ERROR: casting threw an exception.");
         }
      }
      populated = true;
   }

   @Override
   public void navigateTo(String requestedDataId) {
      super.navigateTo(requestedDataId);
      this.requestedDataId = requestedDataId;
      callInitArtifactPage();
   }

   private void callInitArtifactPage() {
      if (searchPresenter != null) {
         searchPresenter.initArtifactPage(requestedDataId, searchHeader, this, relationsComp, attributeComp);
      }
   }

}
