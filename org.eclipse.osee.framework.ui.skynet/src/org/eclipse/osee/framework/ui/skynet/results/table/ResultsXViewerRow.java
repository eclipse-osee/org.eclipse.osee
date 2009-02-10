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
package org.eclipse.osee.framework.ui.skynet.results.table;

import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class ResultsXViewerRow implements IResultsXViewerRow {

   private final List<String> values;
   private final Artifact doubleClickOpenArtifact;

   public ResultsXViewerRow(List<String> values, Artifact doubleClickOpenArtifact) {
      this.doubleClickOpenArtifact = doubleClickOpenArtifact;
      this.values = values;
   }

   public ResultsXViewerRow(List<String> values) {
      this(values, null);
   }

   public ResultsXViewerRow(String[] values, Artifact doubleClickOpenArtifact) {
      this(Arrays.asList(values), doubleClickOpenArtifact);
   }

   public ResultsXViewerRow(String[] values) {
      this(Arrays.asList(values), null);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.results.xresults.IXViewerTestTask#getValue(int)
    */
   @Override
   public String getValue(int col) {
      return values.get(col);
   }

   /**
    * @return the doubleClickOpenArtifact
    */
   public Artifact getDoubleClickOpenArtifact() {
      return doubleClickOpenArtifact;
   }

}
