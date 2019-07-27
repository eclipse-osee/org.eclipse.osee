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
package org.eclipse.osee.ats.ide.editor.tab.workflow.section;

import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public interface IWfeOperationsSection {

   void createAdvancedSection(WorkflowEditor editor, Composite parent, FormToolkit toolkit);

   void createAdminSection(WorkflowEditor editor, Composite parent, FormToolkit toolkit);

}
