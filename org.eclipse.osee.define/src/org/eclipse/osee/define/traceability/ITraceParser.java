/*
 * Created on Apr 1, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.define.traceability;

import java.nio.CharBuffer;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Roberto E. Escobar
 */
public interface ITraceParser {

   public HashCollection<String, String> getTraceMarksByType(CharBuffer fileBuffer);

}
