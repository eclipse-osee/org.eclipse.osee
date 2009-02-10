/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.connection.jini;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.server.ExportException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import net.jini.core.entry.Entry;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.IServicePropertyChangeListener;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jini.service.core.PropertyEntry;

/**
 * @author b1529404
 */
public abstract class JiniConnector implements IServiceConnector {
    protected final static String LINK_PROPERTY = "JINI_CONNECTOR_LINK";
    private final HashMap<Object, ExportInfo> exports = new HashMap<Object, ExportInfo>();
    private final EnhancedProperties properties;
    private final HashSet<IServicePropertyChangeListener> propertyChangeListeners = new HashSet<IServicePropertyChangeListener>();


    private static final class ExportInfo {
	private final Exporter exporter;
	private final Object exportedObject;

	private ExportInfo(Exporter exporter, Object exportedObject) {
	    this.exportedObject = exportedObject;
	    this.exporter = exporter;
	}
    }

    protected JiniConnector() {
	this(new EnhancedProperties());
    }

    protected JiniConnector(EnhancedProperties properties) {
	this.properties = properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.osee.connection.service.IServiceConnector#export(java.lang
     * .Object)
     */
    @Override
    public Object export(Object callback) throws ExportException {
	try {
	    Exporter exporter = createExporter();
	    Object exportedObject = exporter.export((Remote) callback);
	    exports.put(callback, new ExportInfo(exporter, exportedObject));
	    return exportedObject;
	} catch (UnknownHostException e) {
	    throw new ExportException("failed to export", e);
	}
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.osee.connection.service.IServiceConnector#unexport(java.lang
     * .Object)
     */
    @Override
    public void unexport(Object callback) throws Exception {
	ExportInfo info = exports.remove(callback);
	if (info != null) {
	    info.exporter.unexport(false);
	}
    }

    @Override
    public Object findExport(Object callback) {
	ExportInfo info = exports.get(callback);
	if (info != null) {
	    return info.exportedObject;
	}
	return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.osee.connection.service.IServiceConnector#stop()
     */
    @Override
    public void stop() throws Exception {
	for (ExportInfo info : exports.values()) {
	    info.exporter.unexport(false);
	}
	exports.clear();
    }

    private Exporter createExporter() throws UnknownHostException {
	return new BasicJeriExporter(TcpServerEndpoint.getInstance(Network
		.getValidIP().getHostAddress(), 0), new BasicILFactory(null,
			null, Activator.getDefault().getExportClassLoader()), false,
		false);
    }

    protected static void buildPropertiesFromEntries(Entry[] entries,
	    EnhancedProperties properties) {
	for (Entry entry : entries) {
	    if (entry instanceof PropertyEntry) {
		((PropertyEntry) entry).fillProps(properties.asMap());
		// } else if (entry instanceof ServiceInfo) {
		// ServiceInfo si = (ServiceInfo) entry;
		// properties.setProperty("name", si.name);
		// properties.setProperty("model", si.model == null ? "N.A."
		// : si.model);
		// } else if (entry instanceof Comment) {
		// properties.setProperty("comment", ((Comment) entry).comment);
		// } else if (entry instanceof GroupEntry) {
		// properties.setProperty("groups", ((GroupEntry) entry)
		// .getFormmatedString());
		// } else if (entry instanceof StaticStationInfo) {
		// StaticStationInfo ssi = (StaticStationInfo) entry;
		// properties.setProperty("type", ssi.type);
		// properties.setProperty("station", ssi.station);
		// properties.setProperty("mode", ssi.mode);
		// properties.setProperty("date", ssi.dateStarted);
		// properties.setProperty("version", ssi.version);
		// } else if (entry instanceof Name) {
		// properties.setProperty("name", ((Name) entry).name);
		// } else if (entry instanceof VersionEntry) {
		// properties.setProperty("version",
		// ((VersionEntry) entry).version);
		// } else if (entry instanceof TestEntry) {
		// System.out.println("test entry data = "
		// + ((TestEntry) entry).getData());
	    }
	}
    }

    protected Entry[] createEntries() {
	LinkedList<Entry> entries = new LinkedList<Entry>();
	// GroupEntry group = new GroupEntry();
	// group.group =
	// OseeProperties.getInstance().getOseeJiniServiceGroups();
	// entries.add(group);
	// // entries.add(new StaticStationInfo((String) properties
	// // .getProperty("station"), "", (String) properties
	// // .getProperty("type"), "", "", new Date()));
	// entries.add(new ServiceInfo((String) properties.getProperty("name"),
	// "", "", "", "", ""));
	// // entries.add(new TestEntry("this is test data"));
	PropertyEntry entry = new PropertyEntry(properties.asMap());
	assert entry.getProperty("date", null) != null;
	entries.add(entry);
	return entries.toArray(new Entry[entries.size()]);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.osee.connection.service.IServiceConnector#getProperty(java
     * .lang.String, java.lang.String)
     */
    @Override
    public Serializable getProperty(String property, Serializable defaultValue) {
	return properties.getProperty(property, defaultValue);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.osee.connection.service.IServiceConnector#
     * addPropertyChangeListener
     * (org.eclipse.osee.connection.service.IServicePropertyChangeListener)
     */
    @Override
    public void addPropertyChangeListener(
	    IServicePropertyChangeListener listener) {
	propertyChangeListeners.add(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.osee.connection.service.IServiceConnector#
     * removePropertyChangeListener
     * (org.eclipse.osee.connection.service.IServicePropertyChangeListener)
     */
    @Override
    public void removePropertyChangeListener(
	    IServicePropertyChangeListener listener) {
	propertyChangeListeners.remove(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.osee.connection.service.IServiceConnector#setProperty(java
     * .lang.String, java.lang.String)
     */
    @Override
    public void setProperty(String key, Serializable value) {
	properties.setProperty(key, value);
	for (IServicePropertyChangeListener listener : propertyChangeListeners) {
	    listener.propertyChanged(this, key, value);
	}
    }

    protected EnhancedProperties getProperties() {
	return properties;
    }

    public void entriesChanged(Entry[] entries) {
	EnhancedProperties newProps = new EnhancedProperties();
	buildPropertiesFromEntries(entries, newProps);
	for (String key : properties.differences(newProps)) {
	    for (IServicePropertyChangeListener listener : propertyChangeListeners) {
		listener
		.propertyChanged(this, key, properties.getProperty(key));
	    }
	}
    }
}
