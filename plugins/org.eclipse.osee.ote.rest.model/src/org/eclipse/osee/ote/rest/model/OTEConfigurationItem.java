package org.eclipse.osee.ote.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class OTEConfigurationItem {

	private String locationUrl;
	private String md5Digest;
	private String bundleVersion;
	private String bundleName;
	private boolean isOsgiBundle;
	
	public String getLocationUrl() {
		return locationUrl;
	}
	public void setLocationUrl(String locationUrl) {
		this.locationUrl = locationUrl;
	}
	public String getMd5Digest() {
		return md5Digest;
	}
	
	@Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((bundleName == null) ? 0 : bundleName.hashCode());
      result = prime * result + ((bundleVersion == null) ? 0 : bundleVersion.hashCode());
      result = prime * result + (isOsgiBundle ? 1231 : 1237);
      result = prime * result + ((locationUrl == null) ? 0 : locationUrl.hashCode());
      result = prime * result + ((md5Digest == null) ? 0 : md5Digest.hashCode());
      return result;
   }
	
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      OTEConfigurationItem other = (OTEConfigurationItem) obj;
      if (bundleName == null) {
         if (other.bundleName != null)
            return false;
      } else if (!bundleName.equals(other.bundleName))
         return false;
      if (bundleVersion == null) {
         if (other.bundleVersion != null)
            return false;
      } else if (!bundleVersion.equals(other.bundleVersion))
         return false;
      if (isOsgiBundle != other.isOsgiBundle)
         return false;
      if (locationUrl == null) {
         if (other.locationUrl != null)
            return false;
      } else if (!locationUrl.equals(other.locationUrl))
         return false;
      if (md5Digest == null) {
         if (other.md5Digest != null)
            return false;
      } else if (!md5Digest.equals(other.md5Digest))
         return false;
      return true;
   }
   public void setMd5Digest(String md5Digest) {
		this.md5Digest = md5Digest;
	}
	public String getBundleVersion() {
		return bundleVersion;
	}
	public void setBundleVersion(String bundleVersion) {
		this.bundleVersion = bundleVersion;
	}
	public String getBundleName() {
		return bundleName;
	}
	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}
	public String getSymbolicName() {
		return bundleName;
	}
	public String getVersion() {
		return bundleVersion;
	}

	public boolean isOsgiBundle() {
	   return isOsgiBundle;
	}
	public void setOsgiBundle(boolean isOsgiBundle) {
	   this.isOsgiBundle = isOsgiBundle;
	}
	   

}
