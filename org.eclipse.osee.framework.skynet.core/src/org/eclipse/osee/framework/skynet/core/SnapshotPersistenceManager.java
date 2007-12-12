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

package org.eclipse.osee.framework.skynet.core;

import static org.eclipse.osee.framework.ui.plugin.util.db.schemas.SkynetDatabase.SNAPSHOT_TABLE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.ui.plugin.sql.SQL3DataType;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandler;
import org.eclipse.osee.framework.ui.plugin.util.db.ConnectionHandlerStatement;
import org.eclipse.osee.framework.ui.plugin.util.db.DbUtil;
import org.eclipse.osee.framework.ui.plugin.util.db.Query;
import org.eclipse.osee.framework.ui.plugin.util.db.StringRsetProcessor;

/**
 * @author Robert A. Fisher
 */
public class SnapshotPersistenceManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(SnapshotPersistenceManager.class);
   private static final String DROP_SNAPSHOT = "DELETE FROM " + SNAPSHOT_TABLE + " WHERE namespace=? and key=?";
   private static final String INSERT_SNAPSHOT =
         "INSERT INTO " + SNAPSHOT_TABLE + " (NAMESPACE, KEY, LAST_UPDATED, LAST_ACCESSED, OBJECT) VALUES (?,?,?,?,?)";
   private static final String UPDATE_LAST_ACCESSED =
         "UPDATE " + SNAPSHOT_TABLE + " SET last_accessed=? WHERE namespace=? AND key=?";
   private static final String SELECT_SNAPSHOT =
         "SELECT object, last_updated FROM " + SNAPSHOT_TABLE + " WHERE namespace=? AND key=?";
   private static final String SELECT_KEYS = "SELECT key FROM " + SNAPSHOT_TABLE + " WHERE namespace=?";
   private static final SnapshotPersistenceManager instance = new SnapshotPersistenceManager();

   private SnapshotPersistenceManager() {
   }

   public static SnapshotPersistenceManager getInstance() {
      return instance;
   }

   public void persistSnapshot(String namespace, String key, Object object) {
      if (namespace == null) throw new IllegalArgumentException("namespace can not be null.");
      try {
         ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

         outputStream.writeObject(object);

         InputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

         // This call is delayed until after serialization so the last snapshot is not lost if this
         // snapshot fails
         ConnectionHandler.runPreparedUpdate(DROP_SNAPSHOT, SQL3DataType.VARCHAR, namespace, SQL3DataType.VARCHAR, key);

         Timestamp timestamp = GlobalTime.GreenwichMeanTimestamp();
         ConnectionHandler.runPreparedUpdate(INSERT_SNAPSHOT, SQL3DataType.VARCHAR, namespace, SQL3DataType.VARCHAR,
               key, SQL3DataType.TIMESTAMP, timestamp, SQL3DataType.TIMESTAMP, timestamp, SQL3DataType.BLOB,
               inputStream);

      } catch (IOException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      } catch (SQLException e) {
         logger.log(Level.SEVERE, e.toString(), e);
      }
   }

   public Pair<Object, Date> getSnapshot(String namespace, String key) {
      ConnectionHandlerStatement chStmt = null;

      try {
         chStmt =
               ConnectionHandler.runPreparedQuery(1, SELECT_SNAPSHOT, SQL3DataType.VARCHAR, namespace,
                     SQL3DataType.VARCHAR, key);

         ResultSet rset = chStmt.getRset();

         if (rset.next()) {
            ConnectionHandler.runPreparedUpdate(UPDATE_LAST_ACCESSED, SQL3DataType.TIMESTAMP,
                  GlobalTime.GreenwichMeanTimestamp(), SQL3DataType.VARCHAR, namespace, SQL3DataType.VARCHAR, key);

            ObjectInputStream inputStream = new ObjectInputStream(rset.getBinaryStream("object"));

            Object snapshotObject = inputStream.readObject();
            Date snapshotDate = rset.getTimestamp("last_updated");

            return new Pair<Object, Date>(snapshotObject, snapshotDate);
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      } catch (Exception ex) {
         logger.log(Level.WARNING, ex.toString(), ex);
      } finally {
         DbUtil.close(chStmt);
      }
      return null;
   }

   public Collection<String> getKeys(String namespace) throws SQLException {
      Collection<String> keys = new LinkedList<String>();
      Query.acquireCollection(keys, new StringRsetProcessor("key"), SELECT_KEYS, SQL3DataType.VARCHAR, namespace);

      return keys;
   }

   public void deleteAll(String namespace) {
      if (namespace == null) {
         throw new IllegalArgumentException("namespace can not be null.");
      }
      try {
         for (String key : getKeys(namespace)) {
            try {
               ConnectionHandler.runPreparedUpdate(DROP_SNAPSHOT, SQL3DataType.VARCHAR, namespace,
                     SQL3DataType.VARCHAR, key);
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
         }
      } catch (SQLException ex) {
         logger.log(Level.SEVERE, ex.toString(), ex);
      }
   }
}
