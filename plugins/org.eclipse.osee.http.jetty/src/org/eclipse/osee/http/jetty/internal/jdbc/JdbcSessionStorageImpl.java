/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.http.jetty.internal.jdbc;

import static org.eclipse.osee.http.jetty.JettyException.newJettyException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.http.jetty.JettyLogger;
import org.eclipse.osee.http.jetty.internal.session.HttpSessionImpl;
import org.eclipse.osee.http.jetty.internal.session.SessionIdManagerImpl.SessionIdStorage;
import org.eclipse.osee.http.jetty.internal.session.SessionManagerImpl;
import org.eclipse.osee.http.jetty.internal.session.SessionManagerImpl.SessionStorage;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.jdbc.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public class JdbcSessionStorageImpl implements SessionIdStorage, SessionStorage {

   private static final String SELECT_BOUNDED_EXPIRED_SESSIONS =
      "select * from osee_http_session where expires_in >= ? and expires_in <= ?";

   private static final String SELECT_EXPIRED_SESSIONS =
      "select * from osee_http_session where expires_in > 0 and expires_in <= ?";

   private static final String DELETE_OLD_EXPIRED_SESSIONS =
      "delete from osee_http_session where expires_in > 0 and expires_in <= ?";

   private static final String INSERT_ID = "insert into osee_http_session_id (id) values (?)";
   private static final String DELETE_ID = "delete from osee_http_session_id where id = ?";
   private static final String QUERY_ID = "select * from osee_http_session_id where id = ?";

   private static final String INSERT_SESSION =
      "insert into osee_http_session (session_row_id, session_id, context_path, virtual_host, last_node, access_time, last_access_time, created_on, cookie_set_on, last_saved_time, expires_in, attributes) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

   private static final String DELETE_SESSION = "delete from osee_http_session where session_row_id = ?";

   private static final String UPDATE_SESSION =
      "update osee_http_session set last_node = ?, access_time = ?, last_access_time = ?, last_saved_time = ?, expires_in = ?, attributes = ? where session_row_id = ?";

   private static final String UPDATE_SESSION_NODE =
      "update osee_http_session set last_node = ? where session_row_id = ?";

   private static final String UPDATE_SESSION_ACCESS_TIME =
      "update osee_http_session set last_node = ?, access_time = ?, last_access_time = ?, last_saved_time = ?, expires_in = ? where session_row_id = ?";

   private static final String SELECT_NULL_CONTEXT_SESSIONS =
      "select * from osee_http_session where session_id = ? and context_path is null and virtual_host = ?";

   private static final String SELECT_SESSIONS_WITH_CONTEXT =
      "select * from osee_http_session where session_id = ? and context_path = ? and virtual_host = ?";

   private final JettyLogger logger;
   private final JdbcClient jdbcClient;

   public JdbcSessionStorageImpl(JettyLogger logger, JdbcClient jdbcClient) {
      super();
      this.logger = logger;
      this.jdbcClient = jdbcClient;
   }

   @Override
   public void insertSessionId(String id) {
      if (!sessionIdExists(id)) {
         jdbcClient.runPreparedUpdate(INSERT_ID, id);
      }
   }

   @Override
   public void deleteSessionId(String id) {
      jdbcClient.runPreparedUpdate(DELETE_ID, id);
   }

   @Override
   public boolean sessionIdExists(String id) {
      String sessionObject = jdbcClient.runPreparedQueryFetchObject("", QUERY_ID, id);
      return Strings.isValid(sessionObject);
   }

   @Override
   public List<String> cleanExpiredSessions() {
      final List<String> expiredSessionIds = new ArrayList<String>();
      jdbcClient.runTransaction(new JdbcTransaction() {

         @Override
         public void handleTxWork(JdbcConnection connection) {

            long now = System.currentTimeMillis();
            JdbcStatement chStmt = jdbcClient.getStatement(connection);
            try {
               logger.debug("Searching for sessions expired before [%s]", now);
               chStmt.runPreparedQuery(SELECT_EXPIRED_SESSIONS, now);
               while (chStmt.next()) {
                  String sessionId = chStmt.getString("session_id");
                  expiredSessionIds.add(sessionId);
                  logger.debug("Found expired session id [%s]", sessionId);
               }
            } finally {
               chStmt.close();
            }
            if (!expiredSessionIds.isEmpty()) {
               jdbcClient.runPreparedUpdate(
                  connection,
                  createCleanExpiredSessionsSql("delete from osee_http_session where session_id in ", expiredSessionIds));
               jdbcClient.runPreparedUpdate(connection,
                  createCleanExpiredSessionsSql("delete from osee_http_session_id where id in ", expiredSessionIds));
            }

         }

         private String createCleanExpiredSessionsSql(String sql, Collection<String> expiredSessionIds) {
            StringBuffer buff = new StringBuffer();
            buff.append(sql);
            buff.append("(");
            Iterator<String> itor = expiredSessionIds.iterator();
            while (itor.hasNext()) {
               buff.append("'" + (itor.next()) + "'");
               if (itor.hasNext()) {
                  buff.append(",");
               }
            }
            buff.append(")");
            logger.debug("Cleaning expired sessions with: [%s]", buff);
            return buff.toString();
         }
      });
      return expiredSessionIds;
   }

   @Override
   public List<String> getBoundedExpiredSessions(long lowerBound, long upperBound) {
      List<String> expiredSessionIds = new ArrayList<String>();
      logger.debug("Searching for sessions expired between [%s] and [%s]", lowerBound, upperBound);
      JdbcStatement chStmt = jdbcClient.getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_BOUNDED_EXPIRED_SESSIONS, lowerBound, upperBound);
         while (chStmt.next()) {
            String sessionId = chStmt.getString("session_id");
            expiredSessionIds.add(sessionId);
            logger.debug(" Found expired sessionId - [%s]" + sessionId);
         }
      } finally {
         chStmt.close();
      }
      return expiredSessionIds;
   }

   @Override
   public void deleteExpiredSessions(long expiresInMax) {
      logger.debug("Deleting old expired sessions expired before [%s]", expiresInMax);
      int rows = jdbcClient.runPreparedUpdate(DELETE_OLD_EXPIRED_SESSIONS, expiresInMax);
      logger.debug("Deleted [%s] rows of old sessions expired before [%s]", rows, expiresInMax);
   }

   @Override
   public HttpSessionImpl loadSession(SessionManagerImpl sessionManager, String id, String contextPath, String virtualHosts) {
      HttpSessionImpl session = null;
      JdbcStatement chStmt = jdbcClient.getStatement();
      try {
         if (contextPath != null) {
            chStmt.runPreparedQuery(SELECT_SESSIONS_WITH_CONTEXT, id, contextPath, virtualHosts);
         } else {
            chStmt.runPreparedQuery(SELECT_NULL_CONTEXT_SESSIONS, id, virtualHosts);
         }
         if (chStmt.next()) {
            String sessionRowId = chStmt.getString("session_row_id");
            session =
               new HttpSessionImpl(logger, sessionManager, this, id, sessionRowId, chStmt.getLong("created_on"),
                  chStmt.getLong("access_time"));
            session.setCookieSet(chStmt.getLong("cookie_set_on"));
            session.setLastAccessedTime(chStmt.getLong("last_access_time"));
            session.setLastNode(chStmt.getString("last_node"));
            session.setLastSaved(chStmt.getLong("last_saved_time"));
            session.setExpiryTime(chStmt.getLong("expires_in"));
            session.setCanonicalContext(chStmt.getString("context_path"));
            session.setVirtualHost(chStmt.getString("virtual_host"));

            Map<String, Object> attributeMap = readAttributeMap(chStmt.getBinaryStream("attributes"));
            session.setAttributeMap(attributeMap);
         }
      } finally {
         chStmt.close();
      }
      return session;
   }

   @SuppressWarnings({"unchecked"})
   private Map<String, Object> readAttributeMap(InputStream inputStream) {
      ClassLoadingObjectInputStream ois = null;
      try {
         ois = new ClassLoadingObjectInputStream(inputStream);
         return (Map<String, Object>) ois.readObject();
      } catch (IOException ex) {
         throw newJettyException(ex, "Error reading attribute map");
      } catch (ClassNotFoundException ex) {
         throw newJettyException(ex, "Error reading attribute map");
      } finally {
         Lib.close(ois);
      }
   }

   @Override
   public void insertSession(String nodeId, String sessionRowId, HttpSessionImpl session) {
      long now = System.currentTimeMillis();
      Object[] insertData =
         new Object[] {
            sessionRowId,
            session.getId(),
            session.getCanonicalContext(),
            session.getVirtualHost(),
            nodeId,
            session.getAccessed(),
            session.getLastAccessedTime(),
            session.getCreationTime(),
            session.getCookieSet(),
            now,
            session.getExpiryTime(),
            asInputStream(session.getAttributeMap())};

      jdbcClient.runPreparedUpdate(INSERT_SESSION, insertData);
      session.setRowId(sessionRowId); //set it on the in-memory data as well as in db
      session.setLastSaved(now);
   }

   @Override
   public void updateSession(String nodeId, HttpSessionImpl session) {
      long now = System.currentTimeMillis();
      Object rowId = session.getRowId() != null ? session.getRowId() : SQL3DataType.VARCHAR;
      Object[] updateData =
         new Object[] {
            nodeId,
            session.getAccessed(),
            session.getLastAccessedTime(),
            now,
            session.getExpiryTime(),
            asInputStream(session.getAttributeMap()),
            rowId};
      jdbcClient.runPreparedUpdate(UPDATE_SESSION, updateData);
      session.setLastSaved(now);
   }

   @Override
   public void updateSessionNode(String nodeId, HttpSessionImpl session) {
      jdbcClient.runPreparedUpdate(UPDATE_SESSION_NODE, nodeId, session.getRowId());
   }

   @Override
   public void updateSessionAccessTime(String nodeId, HttpSessionImpl session) {
      long now = System.currentTimeMillis();
      jdbcClient.runPreparedUpdate(UPDATE_SESSION_ACCESS_TIME, nodeId, session.getAccessed(),
         session.getLastAccessedTime(), now, session.getExpiryTime(), session.getRowId());
      session.setLastSaved(now);
   }

   @Override
   public void deleteSession(HttpSessionImpl session) {
      jdbcClient.runPreparedUpdate(DELETE_SESSION, session.getRowId());
   }

   private ByteArrayInputStream asInputStream(Map<String, Object> attributes) {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos;
      try {
         oos = new ObjectOutputStream(baos);
         oos.writeObject(attributes);
         return new ByteArrayInputStream(baos.toByteArray());
      } catch (IOException ex) {
         throw newJettyException(ex, "Error serializing http session attribute map [%s]", attributes);
      }
   }

   private static final class ClassLoadingObjectInputStream extends ObjectInputStream {
      public ClassLoadingObjectInputStream(java.io.InputStream in) throws IOException {
         super(in);
      }

      @Override
      public Class<?> resolveClass(java.io.ObjectStreamClass cl) throws IOException, ClassNotFoundException {
         try {
            return Class.forName(cl.getName(), false, Thread.currentThread().getContextClassLoader());
         } catch (ClassNotFoundException e) {
            return super.resolveClass(cl);
         }
      }
   }
}