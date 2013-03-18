package org.eclipse.osee.ote.discovery;

import java.net.URI;
import java.net.URISyntaxException;

public interface OTEServerLocation {

   URI getBrokerURI() throws URISyntaxException;
   URI getApplicationServerURI();
   String getTitle();
   String getMachineName();
   
}
