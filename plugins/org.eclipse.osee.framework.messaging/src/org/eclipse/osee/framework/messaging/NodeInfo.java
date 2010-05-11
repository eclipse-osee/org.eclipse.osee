/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;

import java.io.Serializable;
import java.net.URI;


/**
 * @author Roberto E. Escobar
 */
public class NodeInfo implements Serializable {

	private final URI uri;
	private final String name;
	private String nameWithColon;

	public NodeInfo(String name, URI uri) {
		this.uri = uri;
		this.name = name;
		nameWithColon = name + ":";
	}

	public URI getUri() {
		return uri;
	}

	@Override
	public String toString() {
		return name + ":" + uri;
	}

	public String getComponentName() {
		return name;
	}

	public String getComponentNameForRoutes() {
		return nameWithColon;
	}

	public boolean isVMComponent() {
		return getComponentName().equals(Component.VM.getComponentName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeInfo other = (NodeInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

}
