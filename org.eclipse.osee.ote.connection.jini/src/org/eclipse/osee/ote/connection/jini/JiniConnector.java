/*
 * Created on Jun 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ote.connection.jini;

import java.io.Serializable;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import net.jini.core.entry.Entry;
import net.jini.export.Exporter;
import net.jini.jeri.BasicILFactory;
import net.jini.jeri.BasicJeriExporter;
import net.jini.jeri.tcp.TcpServerEndpoint;
import net.jini.lookup.entry.Comment;
import net.jini.lookup.entry.Name;
import net.jini.lookup.entry.ServiceInfo;

import org.eclipse.osee.connection.service.EnhancedProperties;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.IServicePropertyChangeListener;
import org.eclipse.osee.framework.jdk.core.util.Network;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jini.service.core.GroupEntry;
import org.eclipse.osee.framework.jini.service.core.PropertyEntry;
import org.eclipse.osee.framework.jini.service.core.VersionEntry;
import org.eclipse.osee.framework.jini.service.test.StaticStationInfo;

/**
 * @author b1529404
 */
public abstract class JiniConnector implements IServiceConnector {
   private final HashMap<Object, Exporter> exports = new HashMap<Object, Exporter>();
   private final EnhancedProperties properties;
   private final HashSet<IServicePropertyChangeListener> propertyChangeListeners =
         new HashSet<IServicePropertyChangeListener>();

   protected JiniConnector() {
      this(new EnhancedProperties());
   }

   protected JiniConnector(EnhancedProperties properties) {
      this.properties = properties;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#export(java.lang.Object)
    */
   @Override
   public Object export(Object callback) throws Exception {
      Exporter exporter = createExporter();
      exports.put(callback, exporter);
      return exporter.export((Remote) callback);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#unexport(java.lang.Object)
    */
   @Override
   public void unexport(Object callback) throws Exception {
      Exporter exporter = exports.remove(callback);
      if (exporter != null) {
         exporter.unexport(false);
      }
   }

   @Override
	public Object findExport(Object callback) throws Exception {
		return exports.get(callback);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osee.connection.service.IServiceConnector#stop()
	 */
   @Override
   public void stop() throws Exception {
      for (Exporter exporter : exports.values()) {
         exporter.unexport(false);
      }
      exports.clear();
   }

   private Exporter createExporter() throws UnknownHostException {
      return new BasicJeriExporter(TcpServerEndpoint.getInstance(Network.getValidIP().getHostAddress(), 0),
            new BasicILFactory(null, null, Activator.getDefault().getExportClassLoader()), false, false);
   }

   protected static void buildPropertiesFromEntries(Entry[] entries, EnhancedProperties properties) {
      for (Entry entry : entries) {
         if (entry instanceof PropertyEntry) {
            ((PropertyEntry) entry).fillProps(properties.asMap());
         } else if (entry instanceof ServiceInfo) {
            ServiceInfo si = (ServiceInfo) entry;
            properties.setProperty("name", si.name);
            properties.setProperty("model", si.model == null ? "N.A." : si.model);
         } else if (entry instanceof Comment) {
            properties.setProperty("comment", ((Comment) entry).comment);
         } else if (entry instanceof GroupEntry) {
            properties.setProperty("groups", ((GroupEntry) entry).getFormmatedString());
         } else if (entry instanceof StaticStationInfo) {
            StaticStationInfo ssi = (StaticStationInfo) entry;
            properties.setProperty("type", ssi.type);
            properties.setProperty("station", ssi.station);
            properties.setProperty("mode", ssi.mode);
            properties.setProperty("date", ssi.dateStarted.toString());
            properties.setProperty("version", ssi.version);
         } else if (entry instanceof Name) {
            properties.setProperty("name", ((Name) entry).name);
         } else if (entry instanceof VersionEntry) {
            properties.setProperty("version", ((VersionEntry) entry).version);
         } else if (entry instanceof TestEntry) {
            System.out.println("test entry data = " + ((TestEntry) entry).getData());
         }
      }
   }

   protected Entry[] createEntries() {
      LinkedList<Entry> entries = new LinkedList<Entry>();
      GroupEntry group = new GroupEntry();
      group.group = OseeProperties.getInstance().getOseeJiniServiceGroups();
      entries.add(group);
      entries.add(new StaticStationInfo((String) properties.getProperty("station"), "",
            (String) properties.getProperty("type"), "", "", new Date()));
      entries.add(new ServiceInfo((String) properties.getProperty("name"), "", "", "", "", ""));
      entries.add(new TestEntry("this is test data"));
      //entries.add(new PropertyEntry(properties.asMap()));
      return entries.toArray(new Entry[entries.size()]);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#getProperty(java.lang.String, java.lang.String)
    */
   @Override
   public Serializable getProperty(String property, Serializable defaultValue) {
      return properties.getProperty(property, defaultValue);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#addPropertyChangeListener(org.eclipse.osee.connection.service.IServicePropertyChangeListener)
    */
   @Override
   public void addPropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.add(listener);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#removePropertyChangeListener(org.eclipse.osee.connection.service.IServicePropertyChangeListener)
    */
   @Override
   public void removePropertyChangeListener(IServicePropertyChangeListener listener) {
      propertyChangeListeners.remove(listener);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.connection.service.IServiceConnector#setProperty(java.lang.String, java.lang.String)
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
      System.out.println("props changed!!");
      EnhancedProperties newProps = new EnhancedProperties();
      buildPropertiesFromEntries(entries, newProps);
      for (String key : properties.differences(newProps)) {
         for (IServicePropertyChangeListener listener : propertyChangeListeners) {
            listener.propertyChanged(this, key, properties.getProperty(key));
         }
      }
   }
}
