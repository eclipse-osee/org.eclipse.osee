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
package org.eclipse.osee.coverage.test.package1;

import org.eclipse.osee.coverage.editor.CoverageEditor;
import org.eclipse.osee.coverage.editor.CoverageEditorInput;
import org.eclipse.osee.coverage.model.CoverageOptionManagerDefault;
import org.eclipse.osee.coverage.model.CoveragePackage;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;

/**
 * @author Donald G. Dunne
 */
public class CoveragePackage1 extends XNavigateItemAction {

   public CoveragePackage1() {
      super(null, "");
   }

   public CoveragePackage1(XNavigateItem parent) {
      super(parent, "Open Coverage Package 1");
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      CoveragePackage coveragePackage =
            new CoveragePackage("Coverage Package 1", CoverageOptionManagerDefault.instance());
      CoverageEditor.open(new CoverageEditorInput(coveragePackage.getName(), null, coveragePackage, true));
   }
}
