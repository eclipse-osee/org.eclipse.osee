/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
