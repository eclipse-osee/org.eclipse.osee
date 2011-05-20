/*
 * Created on May 20, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.util;

/**
 * @author Donald G. Dunne
 */
public interface IResultDataListener {

   public void log(XResultData.Type type, String str);
}
