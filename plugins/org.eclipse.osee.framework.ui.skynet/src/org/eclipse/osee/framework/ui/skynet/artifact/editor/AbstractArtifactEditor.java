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
package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
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

   @Override
   protected XFormToolkit createToolkit(Display display) {
      // Create a toolkit that shares colors between editors.
      // the toolkit will be disposed by the super class (FormEditor)
      return new XFormToolkit(SkynetGuiPlugin.getInstance().getSharedFormColors(display));
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
   }

   @Override
   public void doSaveAs() {
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   public void onDirtied() {
   }

   @Override
   public XFormToolkit getToolkit() {
      return (XFormToolkit) super.getToolkit();
   }

   protected Artifact getArtifactFromEditorInput() {
      return (Artifact) getEditorInput().getAdapter(Artifact.class);
   }

   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input);
      ISelectionProvider provider = new ArtifactEditorSelectionProvider();
      Artifact artifact = getArtifactFromEditorInput();
      Object[] selected = artifact != null ? new Object[] {artifact} : EMPTY_ARRAY;
      provider.setSelection(new StructuredSelection(selected));
      getSite().setSelectionProvider(provider);
   }

   private final class ArtifactEditorSelectionProvider implements ISelectionProvider {
      private ISelection selection;

      @Override
      public void addSelectionChangedListener(ISelectionChangedListener listener) {
      }

      @Override
      public ISelection getSelection() {
         return selection;
      }

      @Override
      public void removeSelectionChangedListener(ISelectionChangedListener listener) {
      }

      @Override
      public void setSelection(ISelection selection) {
         this.selection = selection;
      }
   }
}