package org.eclipse.osee.ote.rest.internal;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.ote.rest.model.OteConfiguration;
import org.eclipse.osee.ote.rest.model.OteJobStatus;

public interface OteConfigurationStore {
   OteJobStatus setup(OteConfiguration config, UriInfo uriInfo) throws InterruptedException, ExecutionException, MalformedURLException, IllegalArgumentException, UriBuilderException;
   OteJobStatus reset(UriInfo uriInfo) throws InterruptedException, ExecutionException, MalformedURLException, IllegalArgumentException, UriBuilderException;
	OteConfiguration getConfiguration(UriInfo uriInfo) throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException;
   OteJobStatus getJob(String uuid) throws InterruptedException, ExecutionException;
   Collection<String> getAllJobIds();
}
