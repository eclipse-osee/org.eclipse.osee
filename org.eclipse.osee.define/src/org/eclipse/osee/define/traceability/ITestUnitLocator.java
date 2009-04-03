/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability;

import java.nio.CharBuffer;
import org.eclipse.osee.define.utility.IResourceLocator;

/**
 * @author Roberto E. Escobar
 */
public interface ITestUnitLocator extends IResourceLocator {

   public String UNIT_TYPE_UNKNOWN = "Unknown";

   public String getTestUnitType(String name, CharBuffer fileBuffer);

}
