package org.eclipse.osee.ote.rest.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.core.UriBuilderException;
import javax.ws.rs.core.UriInfo;

import org.eclipse.osee.ote.Configuration;
import org.eclipse.osee.ote.ConfigurationStatus;
import org.eclipse.osee.ote.OTEApi;
import org.eclipse.osee.ote.rest.model.OTEConfiguration;
import org.eclipse.osee.ote.rest.model.OTEJobStatus;

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
   public OTEJobStatus setup(OTEConfiguration config, UriInfo uriInfo) throws InterruptedException, ExecutionException, MalformedURLException, IllegalArgumentException, UriBuilderException {
      Configuration realConfig = TranslateUtil.translateToOtherConfig(config);
      ConfigurationJobStatus status = createConfigurationJobStatus(realConfig, uriInfo);
      Future<ConfigurationStatus> future = null;
      if(config.getInstall()){
         future = ote.loadConfiguration(realConfig, status);
      } else {
         future = ote.downloadConfigurationJars(realConfig, status);
      }
      status.setFuture(future);
      return status.getStatus();
   }

   private ConfigurationJobStatus createConfigurationJobStatus(Configuration config, UriInfo uriInfo) throws MalformedURLException, IllegalArgumentException, UriBuilderException {
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
   public OTEJobStatus reset(UriInfo uriInfo) throws InterruptedException, ExecutionException, MalformedURLException, IllegalArgumentException, UriBuilderException {
      ConfigurationJobStatus status = createConfigurationJobStatus(uriInfo);
      Future<ConfigurationStatus> future = ote.resetConfiguration(status);
      status.setFuture(future);
      return status.getStatus();
   }

   @Override
   public OTEConfiguration getConfiguration(UriInfo uriInfo) throws MalformedURLException, IllegalArgumentException, UriBuilderException, InterruptedException, ExecutionException {
      ConfigurationJobStatus status = createConfigurationJobStatus(uriInfo);
      Future<ConfigurationStatus> future = ote.getConfiguration();
      status.setFuture(future);
      if(future.isDone()){
         return TranslateUtil.translateConfig(future.get().getConfiguration());
      } else {
         return new OTEConfiguration();
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
   public OTEJobStatus getJob(String uuid) throws InterruptedException, ExecutionException {
      return oteJobs.get(uuid);
   }

   @Override
   public Collection<String> getAllJobIds() {
      return oteJobs.getAll();
   }

}
