/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.bit;

import java.util.Collections;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactData;
import org.eclipse.osee.ats.api.workflow.cr.bit.model.BuildImpactDatas;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.Widgets;

/**
 * @author Donald G. Dunne
 */
public class XBitContentProvider implements ITreeContentProvider {

   private final XBitViewer bitViewer;

   public XBitContentProvider(XBitViewer bitViewer) {
      this.bitViewer = bitViewer;
   }

   @Override
   public String toString() {
      return "XBitContentProvider";
   }

   public void clear(boolean forcePend) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (Widgets.isAccessible(bitViewer.getControl())) {
               bitViewer.setInput(Collections.emptyList());
               bitViewer.refresh();
            }
         };
      }, forcePend);
   }

   @Override
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof BuildImpactDatas) {
         BuildImpactDatas bids = (BuildImpactDatas) parentElement;
         return bids.getBuildImpacts().toArray();
      }
      if (parentElement instanceof BuildImpactData) {
         BuildImpactData bid = (BuildImpactData) parentElement;
         return bid.getTeamWfs().toArray();
      }
      return org.eclipse.osee.framework.jdk.core.util.Collections.EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
      if (element instanceof ArtifactToken) {
         return bitViewer.getBids().getTeamWfToBidMap().get(element);
      }
      return null;
   }

   @Override
   public boolean hasChildren(Object element) {
      return getChildren(element).length > 0;
   }

   @Override
   public Object[] getElements(Object inputElement) {
      if (inputElement instanceof String) {
         return new Object[] {inputElement};
      }
      return getChildren(inputElement);
   }

   @Override
   public void dispose() {
      // do nothing
   }

   @Override
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      // do nothing
   }

}
