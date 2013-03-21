package org.eclipse.osee.ote.rest.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.OTEConfiguration;
import org.eclipse.osee.ote.OTEConfigurationStatus;
import org.eclipse.osee.ote.OTEFuture;
import org.eclipse.osee.ote.rest.model.OteConfiguration;
import org.eclipse.osee.ote.rest.model.OteJobStatus;

public class OteConfigurationStoreImpl implements OteConfigurationStore {

   private OTEApi ote;
   private OteJobStore oteJobs;
	
	public OteConfigurationStoreImpl(){
	}
	
	public void bindOTEApi(OTEApi ote){
	   this.ote = ote;
	}
	
	public void unbindOTEApi(OTEApi ote){
	   this.ote = null;
	}
	
	public void bindOteJobStore(OteJobStore oteJobs){
	   this.oteJobs = oteJobs;
	}

	public void unbindOteJobStore(OteJobStore oteJobs){
	   this.oteJobs = null;
	}

   @Override
   public OteJobStatus setup(OteConfiguration config, UriInfo uriInfo) throws InterruptedException, ExecutionException, MalformedURLException, IllegalArgumentException, UriBuilderException {
      OTEConfiguration realConfig = TranslateUtil.translateToOtherConfig(config);
      ConfigurationJobStatus status = createConfigurationJobStatus(config, uriInfo);
      OTEFuture<OTEConfigurationStatus> future = ote.loadConfiguration(realConfig, status);
      status.setFuture(future);
      return status.getStatus();
   }

   private ConfigurationJobStatus createConfigurationJobStatus(OteConfiguration config, UriInfo uriInfo) throws MalformedURLException, IllegalArgumentException, UriBuilderException {
      ConfigurationJobStatus status = new ConfigurationJobStatus();
      status.setId(UUID.randomUUID().toString());
      status.setUrl(generateJobUrl(status, uriInfo));
      oteJobs.add(status);
      return status;
   }

   private URL generateJobUrl(OteJob job, UriInfo uriInfo) throws MalformedURLException, IllegalArgumentException, UriBuilderException{
      return uriInfo.getBaseUriBuilder().path("job").path(job.getId()).build().toURL();
   }
   
   @Override
   public OteJobStatus reset(UriInfo uriInfo) throws InterruptedException, ExecutionException, MalformedURLException, IllegalArgumentException, UriBuilderException {
      ConfigurationJobStatus status = createConfigurationJobStatus(uriInfo);
      OTEFuture<OTEConfigurationStatus> future = ote.resetConfiguration(status);
      status.setFuture(future);
      return status.getStatus();
   }

   @Override
   public OteConfiguration getConfiguration(UriInfo uriInfo) throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException {
      ConfigurationJobStatus status = createConfigurationJobStatus(uriInfo);
      OTEFuture<OTEConfigurationStatus> future = ote.getConfiguration();
      status.setFuture(future);
      if(future.isDone()){
         return TranslateUtil.translateConfig(future.get().getConfiguration());
      } else {
         return new OteConfiguration();
      }
   }

   private ConfigurationJobStatus createConfigurationJobStatus(UriInfo uriInfo) throws MalformedURLException, IllegalArgumentException, UriBuilderException {
      ConfigurationJobStatus status = new ConfigurationJobStatus();
      status.setId(UUID.randomUUID().toString());
      status.setUrl(generateJobUrl(status, uriInfo));
      oteJobs.add(status);
      return status;
   }

   @Override
   public OteJobStatus getJob(String uuid) throws InterruptedException, ExecutionException {
      return oteJobs.get(uuid);
   }

   @Override
   public Collection<String> getAllJobIds() {
      return oteJobs.getAll();
   }

}
