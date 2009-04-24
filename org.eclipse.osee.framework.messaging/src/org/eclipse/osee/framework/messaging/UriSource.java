/*
 * Created on Apr 11, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.io.Serializable;
import java.net.URI;

/**
 * @author b1528444
 *
 */
public class UriSource implements Source, Serializable {

   private static final long serialVersionUID = -917397242786038197L;
   private URI source;
 
   public UriSource(URI source){
      this.source = source;
   }
   
   public URI getSource(){
      return source;
   }

   @Override
   public boolean equals(Object obj) {
      return source.equals(obj);
   }

   @Override
   public int hashCode() {
      return source.hashCode();
   }
}
