/*
 * Created on Jul 17, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.xviewer.column;

import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerPriorityColumn extends XViewerAtsAttributeColumn {

   public XViewerPriorityColumn(boolean show) {
      super(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE, 20, SWT.CENTER, true, SortDataType.String);
   }
}
