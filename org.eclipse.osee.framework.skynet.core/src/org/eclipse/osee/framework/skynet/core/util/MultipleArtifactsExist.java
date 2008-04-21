/*
 * Created on Apr 18, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.util;

/**
 * @author Donald G. Dunne
 */
public class MultipleArtifactsExist extends Exception {

   private static final long serialVersionUID = 1L;

   /**
    * @param arg0
    */
   public MultipleArtifactsExist(String arg0) {
      super(arg0);
   }

}
