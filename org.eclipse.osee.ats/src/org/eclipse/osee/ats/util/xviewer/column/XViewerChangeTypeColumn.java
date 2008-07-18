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
public class XViewerChangeTypeColumn extends XViewerAtsAttributeColumn {

   public XViewerChangeTypeColumn(boolean show) {
      super(ATSAttributes.PRIORITY_TYPE_ATTRIBUTE, 22, SWT.CENTER, true, SortDataType.String);
   }
}
