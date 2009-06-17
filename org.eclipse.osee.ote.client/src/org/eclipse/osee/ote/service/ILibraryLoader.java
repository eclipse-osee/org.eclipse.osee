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
package org.eclipse.osee.ote.service;

public interface ILibraryLoader {
    /**
     * loads a message class dictionary. If one is already loaded then it will
     * be unloaded. Calls the
     * {@link IMessageDictionaryListener#onDictionaryLoaded(IMessageDictionary)}
     * method for all registered {@link IMessageDictionaryListener}s.
     * 
     * @param dictionary
     */
    void loadMessageDictionary(IMessageDictionary dictionary);

    /**
     * unloads the current {@link IMessageDictionary} from the system. This
     * method will call
     * {@link IMessageDictionaryListener#onDictionaryUnloaded(IMessageDictionary)}
     * for each registered {@link IMessageDictionaryListener} before actually
     * unloading the dictionary.
     */
    void unloadMessageDictionary();
    
    /**
     * gets the currently loaded {@link IMessageDictionary}
     * 
     * @return the {@link IMessageDictionary} or null if one is not loaded
     */
    IMessageDictionary getLoadedDictionary();
}
