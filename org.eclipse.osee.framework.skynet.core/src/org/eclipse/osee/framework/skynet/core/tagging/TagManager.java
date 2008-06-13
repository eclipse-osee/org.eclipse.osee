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
package org.eclipse.osee.framework.skynet.core.tagging;

import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TAG_ART_MAP_TABLE;
import static org.eclipse.osee.framework.db.connection.core.schema.SkynetDatabase.TAG_TABLE;
import static org.eclipse.osee.framework.skynet.core.tagging.SystemTagDescriptor.AUTO_INDEXED;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.schema.LocalAliasTable;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IAttributeSaveListener;

/**
 * Manages the tagging of Artifacts in Skynet.
 * 
 * @author Robert A. Fisher
 */
public class TagManager implements IAttributeSaveListener {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(TagManager.class);

   private final CloudDescriptorFactory cloudDescriptorFactory;
   private final TagFactory tagFactory;
   private final TagDescriptorFactory tagDescriptorFactory;

   private static final LocalAliasTable TAG_MAP_ALIAS_1 = TAG_ART_MAP_TABLE.aliasAs("map1");
   private static final LocalAliasTable TAG_ALIAS_1 = TAG_TABLE.aliasAs("tag1");

   private static final String ADD_TAG_TO_ARTIFACT =
         "INSERT INTO " + TAG_ART_MAP_TABLE + " (ART_ID, RELEVANCE, N, BRANCH_ID, TAG_ID) VALUES (?,?,?,?,?)";

   private static final String DROP_TAGS_FROM_ARTIFACT =
         "DELETE FROM " + TAG_ART_MAP_TABLE + " WHERE EXISTS(SELECT 'x' FROM " + Collections.toString(",",
               TAG_MAP_ALIAS_1, TAG_ALIAS_1) + " WHERE " + TAG_ART_MAP_TABLE.join(TAG_MAP_ALIAS_1, "art_id") + " AND " + TAG_ART_MAP_TABLE.join(
               TAG_MAP_ALIAS_1, "branch_id") + " AND " + TAG_ART_MAP_TABLE.join(TAG_MAP_ALIAS_1, "tag_id") + " AND " + TAG_MAP_ALIAS_1.join(
               TAG_ALIAS_1, "tag_id") + " AND " + TAG_MAP_ALIAS_1.column("art_id") + "=? AND " + TAG_MAP_ALIAS_1.column("branch_id") + "=? AND " + TAG_ALIAS_1.column("tag_type_id") + "=?)";
   private static final SkynetActivator plugin = SkynetActivator.getInstance();

   private static final TagManager instance = new TagManager();

   public static TagManager getInstance() {
      return instance;
   }

   private TagManager() {
      this.cloudDescriptorFactory = new CloudDescriptorFactory();
      this.tagFactory = new TagFactory();
      this.tagDescriptorFactory = new TagDescriptorFactory();
   }

   /**
    * Remove all tags of a specific type from an artifact. This is particularly useful with tags that are auto-generated
    * and must be regenerated.
    * 
    * @param artifact The artifact to clear tags from.
    * @param tagDescriptor The type of tags to clear.
    * @throws SQLException
    */
   public static void clearTags(Artifact artifact, TagDescriptor tagDescriptor) throws SQLException {
      ConnectionHandler.runPreparedUpdate(false, DROP_TAGS_FROM_ARTIFACT, SQL3DataType.INTEGER, artifact.getArtId(),
            SQL3DataType.INTEGER, artifact.getBranch().getBranchId(), SQL3DataType.INTEGER,
            tagDescriptor.getTagTypeId());
   }

   /**
    * Perform system level tagging for a set of artifacts if autoTagging is enabled. If autoTagging is disabled then the
    * artifact will just be marked as having stale tag data. The forceTagging boolean can be used to bypass the system
    * check for autoTagging.
    * 
    * @param forceTagging
    * @param artifacts
    * @throws SQLException
    */
   public static synchronized void autoTag(boolean forceTagging, Artifact artifact) throws SQLException {
      if (forceTagging || plugin.isAutoTaggingEnabled()) {
         List<Object[]> tagData = new LinkedList<Object[]>();
         Tagger tagger = TaggerManager.getInstance().getBestTagger(artifact);
         Set<Integer> tagIds = new HashSet<Integer>(); // use a set to force unqiueness on the tag ids

         if (tagger == null) {
            logger.log(Level.WARNING,
                  "Could not auto-tag " + artifact.getDescriptiveName() + ", no tagger was found for it.");
         } else {
            clearTags(artifact, AUTO_INDEXED.getDescriptor());

            for (String tag : tagger.getTags(artifact)) {
               tagIds.add(instance.tagFactory.getTagId(tag, AUTO_INDEXED.getDescriptor()));
            }

            for (String tag : tagger.getTags(artifact)) {
               tagIds.add(instance.tagFactory.getTagId(tag, AUTO_INDEXED.getDescriptor()));
            }

            for (Integer tagId : tagIds) {
               tagData.add(new Object[] {SQL3DataType.INTEGER, artifact.getArtId(), SQL3DataType.INTEGER, null,
                     SQL3DataType.INTEGER, null, SQL3DataType.INTEGER, artifact.getBranch().getBranchId(),
                     SQL3DataType.INTEGER, tagId});
            }
         }
         ConnectionHandler.runPreparedUpdateBatch(ADD_TAG_TO_ARTIFACT, tagData);
      }
   }

   /**
    * Get a collection of all the available cloud descriptors.
    */
   public Collection<CloudDescriptor> getCloudDescriptors() {
      return cloudDescriptorFactory.getDescriptors();
   }

   /**
    * Make a new cloud type.
    * 
    * @return The created cloud descriptor.
    */
   public CloudDescriptor makeCloudType(String cloudTypeName) {
      return cloudDescriptorFactory.createDescriptor(cloudTypeName);
   }

   /**
    * Get a collection of all the available tag descriptors.
    */
   public static Collection<TagDescriptor> getTagDescriptors() {
      return instance.tagDescriptorFactory.getDescriptors();
   }

   /**
    * Make a new tag type.
    * 
    * @return The created tag descriptor.
    */
   public static TagDescriptor makeTagType(String tagTypeName) {
      return instance.tagDescriptorFactory.createDescriptor(tagTypeName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IAttributeSaveListener#notifyOnAttributeSave(org.eclipse.osee.framework.skynet.core.artifact.Artifact[])
    */
   public void notifyOnAttributeSave(Artifact artifact) throws SQLException {
      autoTag(false, artifact);
   }
}