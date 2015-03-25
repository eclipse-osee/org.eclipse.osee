package org.eclipse.osee.ote;

public interface ConfigurationStatusCallback {

   public void success();

   public void failure(String errorLog);

}
