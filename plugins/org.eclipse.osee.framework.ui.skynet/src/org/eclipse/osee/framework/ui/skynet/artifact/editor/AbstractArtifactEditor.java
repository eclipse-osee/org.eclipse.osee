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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.util.SelectionProvider;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractArtifactEditor extends FormEditor implements IDirtiableEditor {

   private final static Object[] EMPTY_ARRAY = new Object[0];
   protected ISelectionProvider defaultSelectionProvider;

   @Override
   protected XFormToolkit createToolkit(Display display) {
      // Create a toolkit that shares colors between editors.
      return new XFormToolkit();
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      // do nothing
   }

   @Override
   public void doSaveAs() {
      // do nothing
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void onDirtied() {
      // do nothing
   }

   @Override
   public XFormToolkit getToolkit() {
      return (XFormToolkit) super.getToolkit();
   }

   public Artifact getArtifactFromEditorInput() {
      return getEditorInput().getAdapter(Artifact.class);
   }

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input);
      defaultSelectionProvider = new SelectionProvider();
      Artifact artifact = getArtifactFromEditorInput();
      Object[] selected = artifact != null ? new Object[] {artifact} : EMPTY_ARRAY;
      defaultSelectionProvider.setSelection(new StructuredSelection(selected));
      getSite().setSelectionProvider(defaultSelectionProvider);
   }

   public ISelectionProvider getDefaultSelectionProvider() {
      return defaultSelectionProvider;
   }

}