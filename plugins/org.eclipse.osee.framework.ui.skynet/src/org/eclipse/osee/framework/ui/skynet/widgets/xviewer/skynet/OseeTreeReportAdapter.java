/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet;

import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public class OseeTreeReportAdapter implements IOseeTreeReportProvider {

   private final String title;

   public OseeTreeReportAdapter(String title) {
      this.title = title;
   }

   @Override
   public String getEditorTitle() {
      return title;
   }

   @Override
   public String getReportTitle() {
      return title;
   }

}
