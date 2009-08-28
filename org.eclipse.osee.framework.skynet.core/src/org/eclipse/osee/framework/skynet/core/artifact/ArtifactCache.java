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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactCache {
   // The keys for this are <artId, transactionId>
   private static final CompositeKeyHashMap<Integer, Integer, Object> historicalArtifactIdCache =
         new CompositeKeyHashMap<Integer, Integer, Object>();
   private static final CompositeKeyHashMap<String, Integer, Object> historicalArtifactGuidCache =
         new CompositeKeyHashMap<String, Integer, Object>();

   private static final CompositeKeyHashMap<Integer, Integer, Object> artifactIdCache =
         new CompositeKeyHashMap<Integer, Integer, Object>(2000);

   private static final CompositeKeyHashMap<String, Integer, Object> artifactGuidCache =
         new CompositeKeyHashMap<String, Integer, Object>(2000);

   private static final CompositeKeyHashMap<String, Branch, Object> keyedArtifactCache =
         new CompositeKeyHashMap<String, Branch, Object>(10);

   private static final HashCollection<String, Object> staticIdArtifactCache =
         new HashCollection<String, Object>(true, HashSet.class, 100);

   private static final HashCollection<ArtifactType, Object> byArtifactTypeCache =
         new HashCollection<ArtifactType, Object>();

   private static final Set<ArtifactType> eternalArtifactTypes = new HashSet<ArtifactType>();

   public static List<Artifact> getArtifactsByName(ArtifactType artifactType, String name) {
      List<Artifact> arts = new ArrayList<Artifact>();
      for (Artifact artifact : getArtifactsByType(artifactType)) {
         if (artifact.getName().equals(name)) {
            arts.add(artifact);
         }
      }
      return arts;
   }

   public static Collection<Artifact> getDirtyArtifacts() throws OseeCoreException {
      Set<Artifact> dirtyArts = new HashSet<Artifact>();
      // ArtifactIdCache is the master cache - no need to check other caches
      for (Entry<Pair<Integer, Integer>, Object> entry : artifactIdCache.entrySet()) {
    	 Artifact art = getArtifact(entry.getValue());
    	 if(art != null){
	         if (art.isDirty()) {
	            dirtyArts.add(art);
	         }
    	 } 
      }
      return dirtyArts;
   }
   
   
   private static Artifact getArtifact(Object obj){
	   if(obj != null){
		   if (obj instanceof Artifact){
			   return (Artifact)obj;
		   } else if (obj instanceof WeakReference){
			   WeakReference<Artifact> art = (WeakReference<Artifact>)obj;
			   return art.get();
		   }
	   }
	   return null;
   }
   

   /**
    * Cache the artifact so that we can avoid creating duplicate instances of an artifact
    * 
    * @param artifact
    * @throws OseeCoreException
    */
   synchronized static void cache(Artifact artifact) throws OseeCoreException {
	  Object obj = getCacheObject(artifact);
      if (artifact.isHistorical()) {
         historicalArtifactIdCache.put(artifact.getArtId(), artifact.getTransactionNumber(), obj);
         historicalArtifactGuidCache.put(artifact.getGuid(), artifact.getTransactionNumber(), obj);
      } else {
         artifactIdCache.put(artifact.getArtId(), artifact.getBranch().getBranchId(), obj);
         artifactGuidCache.put(artifact.getGuid(), artifact.getBranch().getBranchId(), obj);
         byArtifactTypeCache.put(artifact.getArtifactType(), obj);
      }
   }

   private static Object getCacheObject(Artifact artifact){
	  if(eternalArtifactTypes.contains(artifact.getArtifactType())){
		  return artifact;
	  } else if (artifact.isDirty()){
		  return artifact;
	  } else {
		  return new WeakReference<Artifact>(artifact);
	  }
   }
   
   /**
    * This method is called by attributes and relations when their dirty state changes.
    * This way, when an artifact is dirty we can hold onto a strong reference and when it
    * is not dirty we can have a weak reference.
    * 
    * @param artId
    * @param branchId
    * @throws OseeCoreException
    */
   public static void updateCachedArtifact(int artId, int branchId) throws OseeCoreException{
	   Object obj = artifactIdCache.get(artId, branchId);
	   if(obj != null){
		   if (obj instanceof Artifact){
			   Artifact artifact = (Artifact)obj;
			   if(!artifact.isDirty() && !eternalArtifactTypes.contains(artifact.getArtifactType())){
				   cache(artifact);
			   }
		   } else if (obj instanceof WeakReference){
			   WeakReference<Artifact> art = (WeakReference<Artifact>)obj;
			   Artifact artifact = art.get();
			   if(artifact != null && artifact.isDirty()){
				   cache(artifact);
			   }
		   }
	   }
//	   Artifact artifact = getActive(artId, branchId);
//	   if(artifact != null){
//		   cache(artifact);
//	   }
   }
   
   synchronized static void cachePostAttributeLoad(Artifact artifact) throws OseeCoreException {
      for (String staticId : artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)) {
         cacheByStaticId(staticId, artifact);
      }
   }

   public synchronized static void cacheByStaticId(String staticId, Artifact artifact) {
      staticIdArtifactCache.put(staticId, getCacheObject(artifact));
   }

   public synchronized static void cacheByStaticId(Artifact artifact) throws OseeCoreException {
      for (String staticId : artifact.getAttributesToStringList(StaticIdManager.STATIC_ID_ATTRIBUTE)) {
         ArtifactCache.cacheByStaticId(staticId, artifact);
      }
   }

   public synchronized static Collection<Artifact> getArtifactsByStaticId(String staticId) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<Object> cachedArts = staticIdArtifactCache.getValues(staticId);
      if (cachedArts == null) {
         return artifacts;
      }
      for (Object obj : cachedArts) {
    	 Artifact artifact = getArtifact(obj); 
         if (artifact != null && !artifact.isDeleted()) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   public synchronized static Collection<Artifact> getArtifactsByStaticId(String staticId, Branch branch) {
      Set<Artifact> artifacts = new HashSet<Artifact>();
      Collection<Object> cachedArts = staticIdArtifactCache.getValues(staticId);
      if (cachedArts == null) {
         return artifacts;
      }
      for (Object obj : cachedArts) {
    	 Artifact artifact = getArtifact(obj);
         if (artifact != null && !artifact.isDeleted() && artifact.getBranch().equals(branch)) {
            artifacts.add(artifact);
         }
      }
      return artifacts;
   }

   public synchronized static Artifact getHistorical(Integer artId, Integer transactionNumber) {
	  return getArtifact(historicalArtifactIdCache.get(artId, transactionNumber));
   }

   public synchronized static Artifact getHistorical(String guid, Integer transactionNumber) {
	  return getArtifact(historicalArtifactGuidCache.get(guid, transactionNumber));
   }

   public synchronized static List<Artifact> getArtifactsByType(ArtifactType artifactType) {
      List<Artifact> items = new ArrayList<Artifact>();
      Collection<Object> cachedItems = byArtifactTypeCache.getValues(artifactType);
      if (cachedItems != null) {
    	 for(Object obj:cachedItems){
    		 Artifact artifact = getArtifact(obj);
    		 if(artifact != null){
    			 items.add(artifact);
    		 }
    	 }
      }
      return items;
   }

   public static List<Artifact> getArtifactsByType(ArtifactType artifactType, Active active) throws OseeCoreException {
      return Artifacts.getActive(getArtifactsByType(artifactType), active, null);
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branch
    */
   public static Artifact getActive(Integer artId, Branch branch) {
      return getActive(artId, branch.getBranchId());
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branchId
    */
   public synchronized static Artifact getActive(Integer artId, Integer branchId) {
	   return getArtifact(artifactIdCache.get(artId, branchId));
   }

   /**
    * returns the active artifact with the given artifact id from the given branch if it is in the cache and null
    * otherwise
    * 
    * @param artId
    * @param branchId
    */
   public synchronized static Artifact getActive(String artGuid, Integer branchId) {
	   return getArtifact(artifactGuidCache.get(artGuid, branchId));
   }

   /**
    * returns the active artifact based on the previously provided text key and branch
    * 
    * @param key
    * @param branch
    */
   public synchronized static Artifact getByTextId(String key, Branch branch) {
      return getArtifact(keyedArtifactCache.get(key, branch));
   }

   /**
    * used to cache an artifact based on a text identifier and its branch
    * 
    * @param key
    * @param artifact
    */
   public synchronized static Artifact cacheByTextId(String key, Artifact artifact) {
      return getArtifact(keyedArtifactCache.put(key, artifact.getBranch(), getCacheObject(artifact)));
   }


   
   /**
    * Register artifact types that should never be decached
    * 
    * @param artifactType
    */
   public static synchronized void registerEternalArtifactType(ArtifactType artifactType) {
      eternalArtifactTypes.add(artifactType);
   }
   
}
