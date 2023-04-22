/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.publishing;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * This class is used by {@link IRenderer} implementations to store {@link RendererOptions} and there values. It is also
 * used by the server side publishing.
 *
 * @author Branden W. Phillips
 * @author Loren K. Ashley
 */

public class EnumRendererMap implements RendererMap {

   /**
    * Appends the key and value debug information to an exception {@link Message}.
    *
    * @param key the {@link RendererOption} to report.
    * @param value the {@link Object} to report.
    * @param message the {@link Message} that will be appended to.
    * @return the provided <code>message</code>.
    */

   private static Message appendKeyValueMessage(RendererOption key, Object value, Message message) {
      //@formatter:off
      return
         message
            .indentInc()
            .segment( "Key",                  key                                    )
            .segment( "Key Type",             key.getClass()                         )
            .segment( "Value",                value                                  )
            .segment( "Expected Value Class", key.getType().getImplementationClass() )
            .segment( "Value Class"         , Objects.nonNull( value )
                                                 ? value.getClass().getName()
                                                 : "(value is null)"                 )
            ;
      //@formatter:on
   }

   /**
    * Predicate to determine if a scalar value is appropriate for the key.
    *
    * @param key the {@link RendererOption} key.
    * @param value the value to be checked.
    * @return <code>true</code>, when the <code>value</code> is not of the appropriate class; otherwise,
    * <code>false</code>.
    */

   private static boolean checkKeyAndScalarValue(RendererOption key, Object value) {

      if (Objects.isNull(value)) {
         return false;
      }

      var implementationClass = key.getType().getImplementationClass();

      if (Objects.isNull(implementationClass)) {
         return true;
      }

      return implementationClass.isAssignableFrom(value.getClass());

   }

   /**
    * Predicate to determine if a scalar or vector value is appropriate for the key.
    *
    * @param key the {@link RendererOption} key.
    * @param value the value to be checked.
    * @return <code>true</code>, when the <code>value</code> is has ans appropriate class; otherwise,
    * <code>false</code>.
    */

   private static boolean checkKeyAndValue(RendererOption key, Object value) {
      //@formatter:off
      return
         key.getType().isCollection()
            ? EnumRendererMap.checkKeyAndVectorValue( key, value )
            : EnumRendererMap.checkKeyAndScalarValue( key, value );
      //@formatter:on
   }

   /**
    * Predicate to determine if a vector value is appropriate for the key. That is the <code>value</code> must be a
    * collection and it's members must be the appropriate class for the <code>key</code>.
    *
    * @param key the {@link RendererOption} key.
    * @param value the vector value to be checked.
    * @return <code>true</code>, when the <code>value</code> is a collection and it's member are of the appropriate
    * class; otherwise, <code>false</code>.
    */

   private static boolean checkKeyAndVectorValue(RendererOption key, Object value) {
      //@formatter:off
      return
            Objects.nonNull( value )
         && ( value instanceof Collection<?> )
         && !((Collection<?>) value)
               .stream()
               .anyMatch
                  (
                     ( valueElement ) -> !key.getType().getImplementationClass().isAssignableFrom(valueElement.getClass())
                  );
      //@formatter:on
   }

   /**
    * Copies the keys and values that are indicated as copyable from the <code>rendererMap</code> in to the new
    * {@link EnumMap}. Each {@link RendererOption} key has an associated {@link OptionValue}. The {@link OptionValue}
    * objects indicate if the associated {@link RendererOption} key and it's associated value are allowed to be copied.
    *
    * @param rendererMap the {@link Map} to copy values from.
    * @return an {@link EnumMap} with the keys and values from the <code>rendererMap</code>.
    * @throws IllegalArgumentException when the <code>rendererMap</code> contains any values that are invalid for the
    * associated keys.
    */

   private static EnumMap<RendererOption, Object> copy(Map<RendererOption, Object> rendererMap) {

      var enumMap = new EnumMap<>(RendererOption.class);
      //@formatter:off
      rendererMap.keySet().forEach
         (
            ( key ) ->
            {
               var value = rendererMap.get( key );

               if( Objects.nonNull( value ) ) {
                  if( !EnumRendererMap.checkKeyAndValue(key, value) ) {
                     throw
                        new IllegalArgumentException
                               (
                                  new Message()
                                         .title( "EnumRendererMap::copy, parameter \"value\" is not the correct class for the \"key\"." )
                                         .segment( (message) -> EnumRendererMap.appendKeyValueMessage(key,value,message) )
                                         .toString()
                               );
               }

               enumMap.put(key, value);
            }
         });
     //@formatter:on
      return enumMap;
   }

   /**
    * Copies the keys and values that are indicated as copyable from the <code>rendererMap</code> in to new
    * {@link EnumMap}. Each {@link RendererOption} key has an associated {@link OptionValue}. The {@link OptionValue}
    * objects indicated if the associated {@link RendererOption} key and it's associated value are allowed to be copied.
    *
    * @param rendererMap the {@link RenderMap} to copy values from.
    * @return an {@link EnumMap} with the keys and values from the <code>rendererMap</code>.
    * @throws IllegalArgumentException when the <code>rendererMap</code> contains any values that are invalid for the
    * associated keys.
    */

   private static EnumMap<RendererOption, Object> copy(RendererMap rendererMap) {

      var enumMap = new EnumMap<>(RendererOption.class);

      if (Objects.isNull(rendererMap)) {
         return enumMap;
      }

      //@formatter:off
      rendererMap.keySet().forEach
         (
            ( key ) ->
            {
               var value = rendererMap.getRendererOptionValue( key );

               if( !EnumRendererMap.checkKeyAndValue(key, value) ) {
                  throw
                     new IllegalArgumentException
                            (
                               new Message()
                                      .title( "EnumRendererMap::copy, parameter \"value\" is not the correct class for the \"key\"." )
                                      .segment( (message) -> EnumRendererMap.appendKeyValueMessage(key,value,message) )
                                      .toString()
                            );
            }

            enumMap.put(key, value);
         });
         //@formatter:on
      return enumMap;
   }

   /**
    * The map is keyed with {@link RendererOption} enumeration members. Each {@link RendererOption} has an associated
    * {@link OptionType} which specifies the allowed value classes and default values.
    */

   private Map<RendererOption, Object> rendererOptions;

   /**
    * Creates a new empty {@link EnumRendererMap}.
    */

   public EnumRendererMap() {
      this.rendererOptions = new EnumMap<RendererOption, Object>(RendererOption.class);
   }

   /**
    * Creates a new {@link EnumRendererMap} with the entries from <code>rendererMap</code> that are allowed to be
    * copied. The {@link OptionType} associated with each {@link RendererOption} indicates if the value for that
    * {@link RendererOption} is allowed to be copied.
    *
    * @param rendererMap the map of {@link RendererOption} and value to be copied.
    * @throws IllegalArgumentException if any values in the <code>rendererMap</code> are not allowed for the
    * {@link RendererOption} they are associated with.
    */

   public EnumRendererMap(Map<RendererOption, Object> rendererMap) {
      this.rendererOptions = EnumRendererMap.copy(rendererMap);
   }

   /**
    * Creates a new {@link EnumRendererMap} with the specified keys and values.
    *
    * @param objects any number of key value pairs. The number of arguments must be even.
    * @return the new {@link EnumRendererMap}.
    * @throws IllegalArgumentException when:
    * <ul>
    * <li>the number of arguments is odd, or</li>
    * <li>any of the values are not appropriate for the associated keys.</li>
    * </ul>
    */

   public EnumRendererMap(Object... objects) {
      this.rendererOptions = new EnumMap<RendererOption, Object>(RendererOption.class);

      if (Objects.isNull(objects) || objects.length == 0) {
         return;
      }

      if ((objects.length & 1) == 1) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "EnumRendererMap::new, the number of parameters must no be odd." )
                             .indentInc()
                             .segment( "Parameters", objects )
                             .toString()
                   );
         //@formatter:on
      }

      for (var i = 0; i < objects.length; i += 2) {
         var keyObject = objects[i];
         var valueObject = objects[i + 1];

         if (!(keyObject instanceof RendererOption)) {
            throw new IllegalArgumentException("EnumRendererMap::new, the key parameter is not a \"RendererOption\".");
         }

         var key = (RendererOption) keyObject;

         if (!EnumRendererMap.checkKeyAndValue(key, valueObject)) {
            throw new IllegalArgumentException(new Message().title(
               "EnumRendererMap::new, a parameter \"value\" is not the correct class for the \"key\".").segment(
                  (message) -> EnumRendererMap.appendKeyValueMessage(key, valueObject, message)).toString());
         }

         this.rendererOptions.put(key, valueObject);
      }

   }

   /**
    * Creates a new {@link EnumRendererMap} with the entries from <code>rendererMap</code> that are allowed to be
    * copied. The {@link OptionType} associated with each {@link RendererOption} indicates if the value for that
    * {@link RendererOption} is allowed to be copied.
    *
    * @param rendererMap the map of {@link RendererOption} and value to be copied.
    * @throws IllegalArgumentException if any values in the <code>rendererMap</code> are not allowed for the
    * {@link RendererOption} they are associated with.
    */

   public EnumRendererMap(RendererMap rendererMap) {
      this.rendererOptions = EnumRendererMap.copy(rendererMap);
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public void free() {

      this.rendererOptions.clear();
      this.rendererOptions = null;

   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    */

   @Override
   public <T> T getRendererOptionValue(RendererOption key) {

      Objects.requireNonNull(key, "EnumRendererMap::getRendererOptionValue, parameter \"key\" cannot be null.");

      var value = this.rendererOptions.get(key);

      if (Objects.isNull(value)) {
         @SuppressWarnings("unchecked")
         var rv = (T) key.getType().getDefaultValue();
         return rv;
      }

      //@formatter:off
      assert
           EnumRendererMap.checkKeyAndValue(key, value)
         : new Message()
                  .title( "EnumRendererMap::getRendererOptionValue, stored value is not the correct class for the \"key\"." )
                  .segment( (message) -> EnumRendererMap.appendKeyValueMessage(key,value,message) )
                  .toString();
      //@formatter:on

      @SuppressWarnings("unchecked")
      var castValue = (T) value;

      return castValue;

   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public boolean isRendererOptionSet(RendererOption key) {

      Objects.requireNonNull(key, "EnumRendererMap::isRendererOptionSet, parameter \"key\" cannot be null.");

      return this.rendererOptions.containsKey(key);
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public boolean isRendererOptionSetAndFalse(RendererOption key) {

      Objects.requireNonNull(key, "EnumRendererMap::isRendererOptionSetAndFalse, parameter \"key\" cannot be null.");

      if (!key.getType().equals(OptionType.Boolean)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "EnumRendererMap::isRendererOptionSetAndFalse, parameter \"key\" is not for a Boolean option." )
                             .segment( "Key", key )
                             .toString()
                   );
         //@formatter:on
      }

      var value = this.rendererOptions.get(key);

      if (Objects.isNull(value)) {
         return false;
      }

      //@formatter:off
      assert
           ( value instanceof Boolean )
         : new Message()
                  .title( "PublishingOptions::isRendererOptionSetAndFalse, the value associated with the \"key\" is not of the correct class." )
                  .segment( (message) -> EnumRendererMap.appendKeyValueMessage(key,value,message) )
                  .toString();
      //@formatter:on

      var castValue = (Boolean) value;

      return !castValue;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public boolean isRendererOptionSetAndTrue(RendererOption key) {

      Objects.requireNonNull(key, "EnumRendererMap::isRendererOptionSetAndTrue, parameter \"key\" cannot be null.");

      if (!key.getType().equals(OptionType.Boolean)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "EnumRendererMap::isRendererOptionSetAndTrue, parameter \"key\" is not for a Boolean option." )
                             .segment( "Key", key )
                             .toString()
                   );
         //@formatter:on
      }

      var value = this.rendererOptions.get(key);

      if (Objects.isNull(value)) {
         return false;
      }

      //@formatter:off
      assert
           ( value instanceof Boolean )
         : new Message()
                  .title( "PublishingOptions::isRendererOptionSetAndTrue, the value associated with the \"key\" is not of the correct class." )
                  .segment( (message) -> EnumRendererMap.appendKeyValueMessage(key,value,message) )
                  .toString();
      //@formatter:on

      var castValue = (Boolean) value;

      return castValue;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Set<RendererOption> keySet() {
      return Collections.unmodifiableSet(this.rendererOptions.keySet());
   }

   /**
    * {@inheritDoc} throws NullPointerException {@inheritDoc}
    */

   @Override
   public <T> T removeRendererOption(RendererOption key) {

      Objects.requireNonNull(key, "PublishingOptions::removeRendererOption, parameter \"key\" cannot be null.");

      var value = this.rendererOptions.remove(key);

      if (Objects.isNull(value)) {
         return null;
      }

      //@formatter:off
      assert
           EnumRendererMap.checkKeyAndValue(key, value)
         : new Message()
                  .title( "PublishingOptions::removeRendererOption,  the value associated with the \"key\" is not of the correct class." )
                  .segment( (message) -> EnumRendererMap.appendKeyValueMessage(key,value,message) )
                  .toString();
      //@formatter:on

      @SuppressWarnings("unchecked")
      var castValue = (T) value;

      return castValue;
   }

   /**
    * {@inheritDoc}
    *
    * @throws NullPointerException {@inheritDoc}
    * @throws IllegalArgumentException {@inheritDoc}
    */

   @Override
   public <T> T setRendererOption(RendererOption key, T value) {

      Objects.requireNonNull(key, "PublishingOptions::setRendererOption, parameter \"key\" cannot be null.");
      Objects.requireNonNull(value, "PublishingOptions::setRendererOption, parameter \"value\" cannot be null.");

      if (!EnumRendererMap.checkKeyAndValue(key, value)) {
         //@formatter:off
         throw
            new IllegalArgumentException
                   (
                      new Message()
                             .title( "PublishingOptions::setRendererOption, parameter \"value\" is not the correct class for the \"key\".")
                             .segment( (message) -> EnumRendererMap.appendKeyValueMessage(key,value,message) )
                             .toString()
                   );
         //@formatter:on
      }

      var priorValue = this.rendererOptions.put(key, value);

      if (Objects.isNull(priorValue)) {
         return null;
      }

      //@formatter:off
      assert
            EnumRendererMap.checkKeyAndValue(key, priorValue )
         :  new Message()
                  .title( "PublishingOptions::setRendererOption, the prior value associated with the \"key\" is not of the correct class." )
                  .segment( (message) -> EnumRendererMap.appendKeyValueMessage(key,value,message) )
                  .toString();
      //@formatter:on

      @SuppressWarnings("cast")
      var castPriorValue = value;

      return castPriorValue;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {

      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "PublishingOptions" )
         .indentInc()
         .segmentMap( "renderOptions", this.rendererOptions )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public RendererMap unmodifiableRendererMap() {
      return new RendererMap() {

         @Override
         public void free() {
            throw new UnsupportedOperationException();
         }

         @Override
         public <T> T getRendererOptionValue(RendererOption key) {
            return EnumRendererMap.this.getRendererOptionValue(key);
         }

         @Override
         public boolean isRendererOptionSet(RendererOption key) {
            return EnumRendererMap.this.isRendererOptionSet(key);
         }

         @Override
         public boolean isRendererOptionSetAndFalse(RendererOption key) {
            return EnumRendererMap.this.isRendererOptionSet(key);
         }

         @Override
         public boolean isRendererOptionSetAndTrue(RendererOption key) {
            return EnumRendererMap.this.isRendererOptionSetAndTrue(key);
         }

         @Override
         public Set<RendererOption> keySet() {
            return EnumRendererMap.this.keySet();
         }

         @Override
         public <T> T setRendererOption(RendererOption key, T value) {
            throw new UnsupportedOperationException();
         }

         @Override
         public Message toMessage(int indent, Message message) {
            return EnumRendererMap.this.toMessage(indent, message);
         }

         @Override
         public RendererMap unmodifiableRendererMap() {
            return this;
         }

         @Override
         public <T> T removeRendererOption(RendererOption key) {
            throw new UnsupportedOperationException();
         }

      };
   }

}

/* EOF */
