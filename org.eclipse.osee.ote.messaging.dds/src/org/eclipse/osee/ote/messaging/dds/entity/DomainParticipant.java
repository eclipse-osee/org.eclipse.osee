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
package org.eclipse.osee.ote.messaging.dds.entity;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.StatusKind;
import org.eclipse.osee.ote.messaging.dds.listener.DataWriterListener;
import org.eclipse.osee.ote.messaging.dds.listener.DomainParticipantListener;
import org.eclipse.osee.ote.messaging.dds.listener.Listener;
import org.eclipse.osee.ote.messaging.dds.listener.PublisherListener;
import org.eclipse.osee.ote.messaging.dds.listener.SubscriberListener;
import org.eclipse.osee.ote.messaging.dds.listener.TopicListener;
import org.eclipse.osee.ote.messaging.dds.service.DomainId;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;
import org.eclipse.osee.ote.messaging.dds.service.TypeRegistry;
import org.eclipse.osee.ote.messaging.dds.service.TypeSignature;
import org.eclipse.osee.ote.messaging.dds.service.TypeSupport;

/**
 * Provides functionality for applications to participate within a domain as described in the DDS specification. The
 * DomainParticipant is a factory used by the application to create {@link Publisher}'s and {@link Subscriber}'s to
 * interact with the system.
 * <p>
 * In addition to the functionality described in the DDS specification, this class provides particular functionality to
 * interact with middleware which could be used to link this with another data system. The middleware is provided a
 * special publisher to inject data into this system, and receives data from all other publishers by means of the
 * {@link DomainParticipantListener}.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class DomainParticipant extends Entity implements EntityFactory {
   private final DomainId domainId;
   // These collections REALLY need to be thread safe!!!
   private final CopyOnWriteArrayList<Publisher> publishers;
   private final CopyOnWriteArrayList<Subscriber> subscribers;
   private final CompositeKeyHashMap<String, String, Topic> topics;

   private final TypeRegistry typeRegistry;
   private Publisher middlewarePublisher; // Publisher who can send anything

   // DONT_NEED The builtinSubscriber is here for future functionality that is described in the DDS specification but has not been implemented or used.
   private final Subscriber builtinSubscriber;

   /**
    * @param domainId The domain this participant will belong to
    * @param domain A reference to the collection of all participants in this domain.
    * @param enabled Flag which indicates if this is enabled.
    * @param listener The listener attached to this.
    * @param parentFactory A reference to the factory that is creating this.
    * @param threadedPublishing <code>True</code> if we should create a separate thread for processing published data.
    *           If <code>False</code>, the published data will be processed within the thread which makes the call to
    *           write data into the system (or resume publications).
    * @param typeCapacity The initial capacity to use when creating the <code>Map</code> for the {@link TypeRegistry}.
    * @param typeFactor The load factor to use when creating the <code>Map</code> for the {@link TypeRegistry}.
    */
   DomainParticipant(DomainId domainId, Collection<DomainParticipant> domain, boolean enabled, DomainParticipantListener listener, EntityFactory parentFactory, boolean threadedPublishing, int typeCapacity, float typeFactor) {
      super(enabled, listener, parentFactory);

      this.domainId = domainId;
      this.publishers = new CopyOnWriteArrayList<Publisher>(); // Thread Safe
      this.subscribers = new CopyOnWriteArrayList<Subscriber>(); // Thread Safe
      this.topics = new CompositeKeyHashMap<String, String, Topic>(512, true);
      this.middlewarePublisher = null;
      this.typeRegistry = new TypeRegistry(typeCapacity, typeFactor);

      this.builtinSubscriber = null;
   }

   /**
    * Creates the DomainParticipant with default settings for typeCapacity and typeFactor.
    * 
    * @see DomainParticipant#DomainParticipant(DomainId, Collection, boolean, DomainParticipantListener, EntityFactory,
    *      boolean, int, float)
    */
   DomainParticipant(DomainId domainId, Collection<DomainParticipant> domain, boolean enabled, DomainParticipantListener listener, EntityFactory parentFactory, boolean threadedPublishing) {
      this(domainId, domain, enabled, listener, parentFactory, threadedPublishing, 256, .75f);
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    * 
    * @return Returns the builtinSubscriber.
    */
   public Subscriber getBuiltinSubscriber() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) {
         throw new NotImplementedException();
      }
      return builtinSubscriber;
   }

   /**
    * Gets the listener attached to this <code>DomainParticipant</code>.
    */
   public DomainParticipantListener getListener() {
      return (DomainParticipantListener) super.getBaseListener();
   }

   /**
    * Sets the listener attached to this <code>DomainParticipant</code>. This replaces the existing listener.
    * 
    * @param listener The listener to attach.
    * @param mask A mask identifying which communication statuses the listener should be invoked.
    * @return The {@link ReturnCode}returned by {@link Entity#setBaseListener(Listener, StatusKind)}.
    * @see Entity#setBaseListener(Listener, StatusKind)
    */
   public ReturnCode setListener(DomainParticipantListener listener, StatusKind mask) {
      return super.setBaseListener(listener, mask);
   }

   /**
    * Creates a publisher with the passed listener attached.
    * 
    * @param publisherListener The listener to attach to the newly created Publisher.
    * @return The newly created publisher, or null if an error occurred in creation.
    */
   private Publisher createAPublisher(PublisherListener publisherListener) {
      Publisher publisher = null;
      try {
         publisher = new Publisher(this, this.isEnabled(), publisherListener);
         publishers.add(publisher);
      } catch (OutOfMemoryError er) {
         er.printStackTrace();
         publisher = null;
      }

      return publisher;
   }

   /**
    * Creates a middlewarePublisher. If a middlewarePublisher has already been created this will return a reference to
    * the existing middlewarePublisher. The middlewarePublisher is provided as a link for an outside system to be able
    * to inject data into this system.
    * 
    * @param publisherListener The listener to attach to the newly created <code>Publisher</code>. Note that this can be
    *           null.
    * @return A <code>Publisher</code> with the passed in listener assigned to it, or null if an error occurred in
    *         creation.
    */
   public Publisher getMiddlewarePublisherInstance(PublisherListener publisherListener) {
      if (middlewarePublisher == null) {
         middlewarePublisher = createAPublisher(publisherListener);
      }
      return middlewarePublisher;
   }

   /**
    * Creates a new <code>Publisher</code> with a particular listener assigned to it. The publisher will be created
    * enabled iff this <code>DomainParticipant</code> is enabled. If an OutOfMemoryError is thrown while attempting get
    * the new publisher, <b>null </b> will be returned.
    * 
    * @param publisherListener The listener to be attached.
    * @return The newly created <code>Publisher</code>, or null if an error occurred in creation.
    */
   public Publisher createPublisher(PublisherListener publisherListener) {
      Publisher publisher = createAPublisher(publisherListener);
      if (publisher != null) {
         publishers.add(publisher);
      }
      return publisher;
   }

   /**
    * Removes the passed <code>Publisher</code> iff the publisher was created by this <code>DomainParticipant</code> and
    * does not have any attached <code>DataWriter</code> 's.
    * 
    * @param publisher The publisher to be removed.
    * @return {@link ReturnCode#OK}if the publisher was successfully removed, {@link ReturnCode#PRECONDITION_NOT_MET}if
    *         the publisher had writers or was not created by this DomainParticipant, or {@link ReturnCode#ERROR}.
    */
   public ReturnCode deletePublisher(Publisher publisher) {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) {
         return ReturnCode.NOT_ENABLED;
      }

      // Check that a publisher was supplied
      if (publisher == null) {
         return ReturnCode.ERROR;
      }

      // Check the pre-condition
      if (publisher.hasDataWriters()) {
         return ReturnCode.PRECONDITION_NOT_MET;
      }

      // Attempt to remove, if it did not exist in our list then return an error,
      // since it can only be removed from the <code>DomainParticipant</code> which created it.
      if (publishers.remove(publisher)) {
         return ReturnCode.OK;
      } else {
         return ReturnCode.PRECONDITION_NOT_MET;
      }
   }

   /**
    * Creates a <code>Subscriber</code> with the provided listener attached.
    * 
    * @param subscriberListener The listener to be attached.
    * @return The newly created <code>Subscriber</code>, or null if an error occurred in creation.
    */
   public Subscriber createSubscriber(SubscriberListener subscriberListener) {
      Subscriber subscriber = null;
      try {
         subscriber = new Subscriber(this, this.isEnabled(), subscriberListener);
         subscribers.add(subscriber);
      } catch (OutOfMemoryError er) {
         er.printStackTrace();
         subscriber = null;
      }
      return subscriber;
   }

   /**
    * Removes the passed <code>Subscriber</code> iff the subscriber was created by this <code>DomainParticipant</code>
    * and does not have any attached <code>DataReader</code> 's.
    * 
    * @param subscriber The subscriber to be removed.
    * @return {@link ReturnCode#OK}if the subscriber was successfully removed, {@link ReturnCode#PRECONDITION_NOT_MET}if
    *         the subscriber had readers or was not created by this DomainParticipant, or {@link ReturnCode#ERROR}.
    */
   public ReturnCode deleteSubscriber(Subscriber subscriber) {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) {
         return ReturnCode.NOT_ENABLED;
      }

      // Check that a subscriber was supplied
      if (subscriber == null) {
         return ReturnCode.ERROR;
      }

      // Check the pre-condition
      if (subscriber.hasDataReaders()) {
         return ReturnCode.PRECONDITION_NOT_MET;
      }

      // Attempt to remove, if it did not exist in our list then return an error,
      // since it can only be removed from the <code>DomainParticipant</code> which created it.
      if (subscribers.remove(subscriber)) {
         return ReturnCode.OK;
      } else {
         return ReturnCode.PRECONDITION_NOT_MET;
      }
   }

   /**
    * Creates a <code>Topic</code> on this <code>DomainParticipant</code> with the give name. If a <code>Topic</code>
    * already exists with the given name, then the existing topic will be returned IF the typeName matches that of the
    * existing <code>Topic</code>. If the type's do not match, <b>null </b> is returned.
    * 
    * @param name The name of the topic to create.
    * @param typeName The name of the type associated with the topic.
    * @param topicListener The listener to be attached to the created topic if it is newly created. If the topic already
    *           exists, the provided listener is ignored.
    * @return A new <code>Topic</code> if one does not already exist, or an existing topic with the same
    *         <code>name</code> and <code>typeName</code>, otherwise <b>null </b>
    */
   public Topic createTopic(String name, String namespace, String typeName, TopicListener topicListener) {

      Topic topic = null;

      // Even before that, check that the type was registered
      TypeSignature typeSignature = typeRegistry.lookupSignature(typeName);
      if (typeSignature == null) {
         throw new RuntimeException(String.format("No type signature with name [%s] was found.", typeName));
      } else {

         // First check to see if the topic already exists
         TopicDescription topicDescription = lookupTopicDescription(namespace, name);
         if (topicDescription != null && topicDescription instanceof Topic) {

            topic = (Topic) topicDescription;

            // Make sure that the type name is the same, otherwise it is failure
            if (topic.getTypeName().equals(typeName)) {
               // Track that another create has been called for this topic
               topic.incrementCount();
            } else {
               // There is a violation, and no topic will be returned since a topic
               // with this name already exists, but the types do not match
               throw new RuntimeException(
                     String.format(
                           "found topic name:[%s] namespace:[%s] but there was a type incompatibility between [%s] (from topic [%s]) and [%s].",
                           name, namespace, topic.getTypeName(), topic.getName(), typeName));
            }
         } else { // Otherwise, the topic did not already exist
            topic = new Topic(this, typeSignature, name, namespace, this.isEnabled(), topicListener, this);
            topics.put(namespace, name, topic);
         }
      }
      return topic;
   }

   /**
    * This method deletes a <code>Topic</code> previously created by this <code>DomainParticipant</code>. No action will
    * be taken if the topic still has data readers/writers attached to it, or if it was not created by this
    * <code>DomainParticipant</code>.
    * 
    * @param topic The topic to delete.
    * @return {@link ReturnCode#OK}if the topic was successfully removed, {@link ReturnCode#PRECONDITION_NOT_MET}if the
    *         topic has readers/writers or was not created by this DomainParticipant, {@link ReturnCode#NOT_ENABLED}if
    *         this DomainParticipant is not enabled, or {@link ReturnCode#ERROR}.
    */
   public ReturnCode deleteTopic(Topic topic) {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) {
         return ReturnCode.NOT_ENABLED;
      }

      // Check the pre-condition
      if (topic.hasDataReaders()) {
         return ReturnCode.PRECONDITION_NOT_MET;
      }
      // Check the pre-condition
      if (topic.hasDataWriters()) {
         return ReturnCode.PRECONDITION_NOT_MET;
      }

      // Check that a topic was supplied
      if (topic == null) {
         return ReturnCode.ERROR;
      }

      // Attempt to remove, if it did not exist in our list then return an error,
      // since it can only be removed from the <code>DomainParticipant</code> which created it.
      if (topics.containsKey(topic.getNamespace() + topic.getName())) {
         // Reduce the count of creations by 1
         topic.decrementCount();

         // If the creation count is zero, then remove it from the list
         if (topic.getCount() <= 0) {
            topics.removeValues(topic.getNamespace() + topic.getName());
         }

         return ReturnCode.OK;
      } else {
         return ReturnCode.PRECONDITION_NOT_MET;
      }
   }

   /**
    * Get a <code>TopicDescription</code> that matches a particular name. PARTIAL: Since the
    * <code>ContentFilteredTopic</code> and <code>MultiTopic</code> classes are not implemented, this method simply
    * searches the list of <code>Topic</code> classes.
    * 
    * @param namespace The name to match against the <code>TopicDescription</code>.
    * @return The <code>TopicDescription</code> that has the specified name, <b>null </b> if no such one exists.
    */
   public TopicDescription lookupTopicDescription(String namespace, String topic) {
      //PARTIAL ContentFilteredTopic and MultiTopic are not implemented, so this only searches Topic.
      return findTopicByName(namespace, topic);
   }

   /**
    * Searches the current list of Topics for this DomainParticipant for a topic whose name matches the provided
    * topicName
    * 
    * @param topicName The topic name to match
    * @return A reference to the existing topic whose name matches topicName, or null if no such topic exists.
    */
   private Topic findTopicByName(String namespace, String topicName) {
      return topics.get(namespace, topicName);
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    * 
    * @return {@link ReturnCode#OK}if the participant was successfully set to be ignored, {@link ReturnCode#NOT_ENABLED}
    *         if this DomainParticipant is not enabled, or {@link ReturnCode#ERROR}.
    */
   public ReturnCode ignoreParticipant() {
      // UNSURE this is stubbed for now, until we determine it's necessity

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) {
         return ReturnCode.NOT_ENABLED;
      }

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    * 
    * @return {@link ReturnCode#OK}if the topic was successfully set to be ignored, {@link ReturnCode#NOT_ENABLED}if
    *         this DomainParticipant is not enabled, or {@link ReturnCode#ERROR}.
    */
   public ReturnCode ignoreTopic() {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) {
         throw new NotImplementedException();
      }

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) {
         return ReturnCode.NOT_ENABLED;
      }

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    * 
    * @return {@link ReturnCode#OK}if the publication was successfully set to be ignored, {@link ReturnCode#NOT_ENABLED}
    *         if this DomainParticipant is not enabled, or {@link ReturnCode#ERROR}.
    */
   public ReturnCode ignorePublication() {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) {
         throw new NotImplementedException();
      }

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) {
         return ReturnCode.NOT_ENABLED;
      }

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    * 
    * @return {@link ReturnCode#OK}if the subscription was successfully set to be ignored,
    *         {@link ReturnCode#NOT_ENABLED}if this DomainParticipant is not enabled, or {@link ReturnCode#ERROR}.
    */
   public ReturnCode ignoreSubscription() {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) {
         throw new NotImplementedException();
      }

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) {
         return ReturnCode.NOT_ENABLED;
      }

      return ReturnCode.ERROR;
   }

   /**
    * Gets the <code>DomainId</code> that this <code>DomainParticipant</code> belongs to.
    * 
    * @return The <code>DomainId</code> of this participant.
    */
   public DomainId getDomainId() {
      return domainId;
   }

   /**
    * Performs a deep deletion of contained entities (Publishers, Subscribers, Topics). This will cause a recursive call
    * of <code>deleteContainedEntities</code> through out all of the contained entities. This can only be performed if
    * the <code>DomainParticipant</code> is enabled.
    * 
    * @return {@link ReturnCode#OK}if the all entities were successfully deleted, or {@link ReturnCode#NOT_ENABLED}if
    *         this DomainParticipant is not enabled.
    */
   public ReturnCode deleteContainedEntities() {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) {
         return ReturnCode.NOT_ENABLED;
      }

      for (Publisher publisher : publishers) {
         publisher.deleteContainedEntities();
      }
      publishers.clear();

      for (Subscriber subscriber : subscribers) {
         subscriber.deleteContainedEntities();
      }

      subscribers.clear();

      this.middlewarePublisher = null;

      // Topics do not contain anything else
      topics.clear();

      return ReturnCode.OK;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    */
   public void assertLiveliness() {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) {
         throw new NotImplementedException();
      }
   }

   /**
    * Returns true iff this <code>DomainParticipant</code> contains at least one publisher, subscriber or topic.
    * 
    * @return <b>true </b> if this contains an entity, <b>false </b> otherwise.
    */
   public boolean hasEntities() {
      return !(publishers.isEmpty() && subscribers.isEmpty() && topics.isEmpty());
   }

   /**
    * Propagates the published data to each <code>Subscriber</code> of this <code>DomainParticipant</code>. Notifies the
    * middleware (by means of the {@link DomainParticipantListener}of the new data unless it originated from the
    * middlewarePublisher. The middleware is only notified of data originating from this <code>DomainPariticipant</code>
    * , that is, data published from other participants in the domain will not be sent to the middleware.
    * 
    * @param destination TODO
    * @param source TODO
    * @param dataStoreItem The <code>DataStoreItem</code> that was published.
    */
   void processPublishedData(IDestination destination, ISource source, DataStoreItem dataStoreItem) { // package scope since it is a
      // system-level call

      // Notify the middleware of new data, unless it was published by the middlewarePublisher
      // When data is published by the middlewarePublisher it does not have an associated DataWriter

      DataWriter writer = dataStoreItem.getTheWriter();
      if (writer != null) {
         // Invoke the DomainParticipantListener if available
         DomainParticipantListener domainParticipantListener = getListener();
         if (domainParticipantListener != null) {
            domainParticipantListener.onPublishNotifyMiddleware(destination, source, dataStoreItem);
         }

         // If the writer has a listener, then invoke it
         DataWriterListener writerListener = writer.getListener();
         if (writerListener != null) {
            writerListener.onDataSentToMiddleware(writer);
         }

      }

      if (writer == null || writer != null && writer.isPublishBackToLocalDDSReaders()) {
         // Notify all of the subscribers in our domain
         for (Subscriber domainSubscribers : subscribers) {
            domainSubscribers.processNewData(dataStoreItem);
         }
      }
   }

   /**
    * Gets the <code>TypeRegistry</code> used by {@link TypeSupport}to store the types registered for this
    * <code>DomainParticipant</code>.
    * 
    * @return Returns the typeRegistry.
    */
   public TypeRegistry getTypeRegistry() {
      return typeRegistry;
   }

   /**
    * Gets the Middleware <code>Publisher</code> if one has been created.
    * 
    * @return Returns the <code>Publisher</code> for the middleware, or null if it has not been created.
    * @see DomainParticipant
    */
   public Publisher getMiddlewarePublisher() {
      return middlewarePublisher;
   }

   /**
    * Gets the current <code>Collection</code> of <code>Publisher</code>'s for this <code>DomainPariticpant</code>.
    * 
    * @return Returns the <code>Collection</code> of <code>Publisher</code> 's.
    */
   public CopyOnWriteArrayList<Publisher> getPublishers() {
      return publishers;
   }

   public void dispose() {
      for (Topic topic : topics.values()) {
         topic.clearDataReaders();
         topic.clearDataWriters();
      }
      topics.clear();
      for (Subscriber subscriber : subscribers) {
         if (subscriber.deleteContainedEntities() == ReturnCode.NOT_ENABLED) {
            System.err.println("failed to delete subscriber because it was not enabled");
         }
      }
      subscribers.clear();

      for (Publisher publisher : publishers) {
         publisher.dispose();
      }
      publishers.clear();

      typeRegistry.clear();
   }
}
