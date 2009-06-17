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
package org.eclipse.osee.ote.ui.test.manager;

import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.pages.AdvancedPage;
import org.eclipse.osee.ote.ui.test.manager.pages.ScriptPage;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Add a summary of extension points and other classes that will need to be added to create a new test manager.
 * 
 * @author Roberto E. Escobar
 */
public interface ITestManagerFactory {

   public AdvancedPage getAdvancedPageNewInstance(Composite parent, int style, TestManagerEditor parentTestManager);

   public String getEditorId();

   public String getEditorLastOpenedKey();
   
   public ScriptPage getScriptPageNewInstance(Composite parent, int style, TestManagerEditor parentTestManager);

   public String getTestManagerExtension();

   public String getTestManagerFileName();

}
