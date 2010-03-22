/*
 * Created on Mar 17, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.server;

/**
 * @author  Andrew M. Finkbeiner
 */
public class PropertyParamter {
   private final String type;
   private final String version;
   private final String comment;
   private final String station;
   private final boolean useJiniLookup;
   private final boolean isLocalConnector;
   
   public PropertyParamter(String version, String comment, String station, String type, boolean useJiniLookup, boolean isLocalConnector) {
      this.version = version;
      this.comment = comment;
      this.station = station;
      this.type = type;
      this.useJiniLookup = useJiniLookup;
      this.isLocalConnector = isLocalConnector;
   }

   public String getType() {
      return type;
   }

   public String getVersion() {
      return version;
   }

   public String getComment() {
      return comment;
   }

   public String getStation() {
      return station;
   }

   public boolean isLocalConnector() {
      return isLocalConnector;
   }

   public boolean useJiniLookup() {
      return useJiniLookup;
   }

}