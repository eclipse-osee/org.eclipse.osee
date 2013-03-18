package org.eclipse.osee.ote.discovery;

import java.net.URISyntaxException;
import java.util.List;

public interface OTEServerDiscovery {
   
   List<OTEServerLocation> findServerByTitle(String regex) throws URISyntaxException;
   List<OTEServerLocation> findServerByMachine(String regex) throws URISyntaxException;
   List<OTEServerLocation> findServerByMachineAndTitle(String regexMachine, String regexTitle) throws URISyntaxException;
   List<OTEServerLocation> getAll() throws URISyntaxException;
   List<OTEServerLocation> findServerByTitle(String regex, long timeoutMs) throws URISyntaxException;
   List<OTEServerLocation> findServerByMachine(String string, long timeoutMs) throws URISyntaxException;
   List<OTEServerLocation> findServerByMachineAndTitle(String regexMachine, String regexTitle, long timeoutMs) throws URISyntaxException;
   
}
