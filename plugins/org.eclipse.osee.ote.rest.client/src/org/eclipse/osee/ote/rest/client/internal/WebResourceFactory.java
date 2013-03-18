package org.eclipse.osee.ote.rest.client.internal;

import java.net.URI;

import com.sun.jersey.api.client.WebResource;

public interface WebResourceFactory {

   WebResource createResource(URI uri) throws Exception;

}
