/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.traceability.editor;

import java.rmi.activation.Activator;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;

public class TraceabilityContentProvider implements ITreeContentProvider {

   protected Collection<IResultsXViewerRow> rootSet = new HashSet<>();
   private static Object[] EMPTY_ARRAY = new Object[0];

   public TraceabilityContentProvider() {
      super();
   }

   @Override
   @SuppressWarnings("rawtypes")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Object[]) {
         return (Object[]) parentElement;
      }
      if (parentElement instanceof Collection) {
         return ((Collection) parentElement).toArray();
      }
      if (parentElement instanceof IResultsXViewerRow) {
         IResultsXViewerRow row = (IResultsXViewerRow) parentElement;
         Object data = row.getData();
         if (data instanceof Artifact) {
            if (((Artifact) data).isOfType(CoreArtifactTypes.AbstractSoftwareRequirement)) {
               List<Object> children = new LinkedList<>();
               try {
                  for (Artifact art : ((Artifact) data).getRelatedArtifacts(CoreRelationTypes.Verification__Verifier)) {
                     children.add(new ResultsXViewerRow(new String[] {art.getName(), "Verifies"}, art));
                  }
                  for (Artifact art : ((Artifact) data).getRelatedArtifacts(CoreRelationTypes.Uses__TestUnit)) {
                     children.add(new ResultsXViewerRow(new String[] {art.getName(), "Uses"}, art));
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.WARNING, ex);
               }
               return children.toArray();
            }
         }
      }
      return EMPTY_ARRAY;
   }

   @Override
   public Object getParent(Object element) {
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

   /**
    * @return the rootSet
    */
   public Collection<IResultsXViewerRow> getRootSet() {
      return rootSet;
   }

}