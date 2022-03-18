/*********************************************************************
 * Copyright (c) 2022 Boeing
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

/**
 * This is a private package of the <code>org.eclipse.osee.synchronization.rest</code> OSGi bundle which implements the
 * the JAX-RS {@link javax.ws.rs.core.Application} for processing requests to the "synchronization" REST API end point.
 * <p>
 * The Synchronization Artifact REST API end point is primarily designed to produce ReqIF artifacts. ReqIF is an OMG
 * industry standard for the exchange of requirements data. The package is designed for creating the necessary parts of
 * a ReqIF XML document and the classes are named using the ReqIF terminology. The creation of the ReqIF classes and the
 * ReqIF document are implemented in a separate package with a class implementing the
 * {@link SynchronizationArtifactBuilder} interface. This separation is to accommodate the production of synchronization
 * artifacts with other formats as long as those formats do not require any data from the ORCS API that is not also
 * needed for a ReqIF document.
 * <h2>Terminology</h2>
 * <dl>
 * <dt>Thing</dt>
 * <dd style="margin-bottom:1em">To disambiguate the use of "object" from Java Objects and from ReqIF SpecificationGroveThing
 * Objects, the classes used as components of the Synchronization Artifact DOM, the native OSEE DOM, and the foreign
 * (ReqIF) DOM are referred to as "things".</dd>
 * <dt>Grove</dt>
 * <dd style="margin-bottom:1em">A grove refers to a collection of map like or tree like data structures.</dd>
 * <dt>Synchronization Artifact DOM</dt>
 * <dd style="margin-bottom:1em">The Synchronization Artifact DOM is an intermediate Document Object Model that is built
 * from the OSEE artifacts to be included in the produced Synchronization Artifact.</dd>
 * <dt>Native DOM</dt>
 * <dd style="margin-bottom:1em">The OSEE artifacts assessed with the ORCS API to be included in the produced
 * Synchronization Artifact are referred to collectively as the Native DOM.</dd>
 * <dt>Foreign DOM</dt>
 * <dd style="margin-bottom:1em">The ReqIF or other Document Object Model that is produced and serialized for the
 * Synchronization Artifact is referred to as the Foreign DOM.</dd>
 * </dl>
 * <h2>Data Flow</h2>
 *
 * <pre>
Synchronization          +-----------------------------------------+
Artifact REST    ------->| SynchronizationEndpointImpl             |
API request              |           create()                      |
                         +-----------------------------------------+
                                        |
                                        | Empty Synchronization Artifact DOM
                                        |
                                        V
                         +-----------------------------------------+
                         | SynchronizationArtifact             |
ORCS API---------------->|           build()                       |
                         | Get native OSEE things from ORCS API    |
                         | and populate the Synchronization        |
                         | Artifact DOM                            |
                         +-----------------------------------------+
                                        |
                                        | Synchronization Artifact DOM with native OSEE Data
                                        |
                                        V
                         +-----------------------------------------+                        +----------------------------------+
                         | SynchronizationArtifact             |<---- Converters--------| SynchronizationArtifactBuilder   |
                         |           build()                       |                        |           getConverter()         |
                         | Applies converters to each native thing |                        | Supplies methods to convert from |
                         | in the Synchronization Artifact DOM to  |                        | native OSEE things to foreign    |
                         | create the foreign things.              |                        | things.                          |
                         +-----------------------------------------+                        +----------------------------------+
                                        |
                                        | Synchronization Artifact DOM with foreign things
                                        |
                                        V
                         +-----------------------------------------+
                         | SyncronizationArtifactBuilder           |
                         |           build()                       |
                         | Constructs the foreign DOM from the     |
                         | foreign things in the Synchronization   |
                         | Artifact DOM.                           |
                         +-----------------------------------------+
                                        |
                                        | Synchronization Artifact with built foreign DOM
                                        |
                                        V
                         +-----------------------------------------+
                         | SynchronizationArtifact             |
                         |          serialize()                    |
                         +-----------------------------------------+
                                        |
                                        | Foreign DOM as octet stream
                                        V
 * </pre>
 *
 * To add support for additional Synchronization Artifact types the following needs to be implemented:
 * <ul>
 * <li>In a new package private to the OSGi bundle, create a class with the annotation
 * {@link IsSynchronizationArtifactBuilder} that implements the interface {@link SynchronizationArtifactBuilder}.</li>
 * <li>Set the <code>synchronizationArtifactType</code> parameter of the {@link IsSynchronizationArtifactBuilder} to a
 * unique {@link String} identifying the type of synchronization artifact the builder class is for.</li>
 * </ul>
 *
 * @author Loren K. Ashley
 */

package org.eclipse.osee.synchronization.rest;

/* EOF */