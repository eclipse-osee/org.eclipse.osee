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
package org.eclipse.osee.ats.view.web.search;

import org.eclipse.osee.ats.api.search.AtsWebSearchPresenter;
import org.eclipse.osee.ats.view.web.AtsAppData;
import org.eclipse.osee.ats.view.web.components.AtsSearchHeaderComponent;
import org.eclipse.osee.display.view.web.search.OseeSearchHeaderComponent;
import org.eclipse.osee.display.view.web.search.OseeSearchHomeView;

/**
 * @author Shawn F. Cook
 */
@SuppressWarnings("serial")
public class AtsSearchHomeView extends OseeSearchHomeView {

   private final AtsWebSearchPresenter atsBackend;

   public AtsSearchHomeView() {
      super();
      atsBackend = AtsAppData.getAtsWebSearchPresenter();
   }

   @Override
   protected OseeSearchHeaderComponent getOseeSearchHeader() {
      return new AtsSearchHeaderComponent(true);
   }

   @Override
   protected void initComponents() {
      if (atsBackend != null) {
         atsBackend.initSearchHome((AtsSearchHeaderComponent) oseeSearchHeader);
      }
   }
}
