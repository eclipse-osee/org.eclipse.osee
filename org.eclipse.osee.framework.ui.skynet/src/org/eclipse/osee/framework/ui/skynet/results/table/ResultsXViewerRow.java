/*
 * Created on Jun 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
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
