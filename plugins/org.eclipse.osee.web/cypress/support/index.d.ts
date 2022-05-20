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
declare namespace Cypress {
  interface Chainable {
    selectBranch(
      name: string,
      type: 'working' | 'baseline'
    ): Chainable<JQuery<HTMLElement>>;
    selectBranchType(type: 'working' | 'baseline'): Chainable<any>; //really dislike this, but Chainable<Interception> is not available right now as a type
    createBranch(
      fromBranch: string,
      actionableItem: string,
      targetedVersion: string,
      title: string,
      description: string
    ): Chainable<any>;
    openMIMUserDialog(): Chainable<JQuery<HTMLElement>>;
    closeMIMUserDialog(): Chainable<JQuery<HTMLElement>>;
    waitForMIMUserDialog(): Chainable<JQuery<HTMLElement>>;
    setUserMIMEditPreference(value: boolean): Chainable<JQuery<HTMLElement>>;
    setUserMIMColumnPreferences(
      ...preferences: string[]
    ): Chainable<JQuery<HTMLElement>>;
    resetColumnPrefsToDefault(): Chainable<JQuery<HTMLElement>>;
    enableMIMEditing(): Chainable<JQuery<HTMLElement>>;
    disableMIMEditing(): Chainable<JQuery<HTMLElement>>;
    createNewPlatformType(type: string): Chainable<JQuery<HTMLElement>>;
    createNode(
      name: string,
      description: string,
      color: string,
      address: string
    ): Chainable<JQuery<HTMLElement>>;
    deleteNode(name: string): Chainable<JQuery<HTMLElement>>;
    createConnection(
      fromNode: string,
      toNode: string,
      name: string,
      description: string,
      transportType: string
    ): Chainable<JQuery<HTMLElement>>;
    deleteConnection(name: string): Chainable<JQuery<HTMLElement>>;
    navigateToConnectionPage(): Chainable<JQuery<HTMLElement>>;
    /**
     * Assumes user is already on connection page
     * @param connectionName
     */
    navigateToMessagePage(connectionName: string): Chainable<string>;
    /**
     * Assumes user is already on message page
     * @param message
     * @param submessage
     * @param isMessageFirst
     */
    navigateToStructurePage(
      message: string,
      submessage: string,
      isMessageFirst: boolean
    ): Chainable<string>;
    createMessage(
      name: string,
      description: string,
      rate: string,
      periodicity: string,
      messageType: string,
      messageNumber: string,
      nodeIsFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    editMessageDescription(
      name: string,
      description: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    createSubMessage(
      associatedMessage: string,
      name: string,
      description: string,
      subMessageNumber: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    editSubMessageTableDescription(
      associatedMessage: string,
      name: string,
      description: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    openMessage(
      name: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    closeMessage(
      name: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    subMessageDialogNew(
      name: string,
      description: string,
      subMessageNumber: string
    ): Chainable<JQuery<HTMLElement>>;
    subMessageRightClick(
      associatedMessage: string,
      submessage: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    insertSubMessageAfter(
      associatedMessage: string,
      submessage: string,
      submessageOffset: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    insertSubMessageTop(
      associatedMessage: string,
      submessage: string,
      submessageOffset: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    insertSubMessageBottom(
      associatedMessage: string,
      submessage: string,
      submessageOffset: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    deleteSubMessage(
      associatedMessage: string,
      submessage: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    removeSubMessage(
      associatedMessage: string,
      submessage: string,
      isMessageFirst: boolean
    ): Chainable<JQuery<HTMLElement>>;
    createStructure(
      name: string,
      description: string,
      maxSimultaneity: string,
      minSimultaneity: string,
      taskFileType: string,
      category: string
    ): Chainable<JQuery<HTMLElement>>;
    createStructureDialog(
      name: string,
      description: string,
      maxSimultaneity: string,
      minSimultaneity: string,
      taskFileType: string,
      category: string
    ): Chainable<JQuery<HTMLElement>>;
    structureRightClick(structure: string): Chainable<JQuery<HTMLElement>>;
    insertStructureTop(
      associatedStructure: string
    ): Chainable<JQuery<HTMLElement>>;
    insertStructureBottom(
      associatedStructure: string
    ): Chainable<JQuery<HTMLElement>>;
    removeStructure(
      associatedStructure: string
    ): Chainable<JQuery<HTMLElement>>;
    deleteStructure(
      associatedStructure: string
    ): Chainable<JQuery<HTMLElement>>;
    editStructureDescription(
      name: string,
      description: string
    ): Chainable<JQuery<HTMLElement>>;
    validateStructureHeaderExists(
      header: string
    ): Chainable<JQuery<HTMLElement>>;
    editFreeText(text: string): Chainable<JQuery<HTMLElement>>;
    openStructure(structure: string): Chainable<JQuery<HTMLElement>>;
    closeStructure(structure: string): Chainable<JQuery<HTMLElement>>;
    createElement(
      name: string,
      description: string,
      notes: string,
      startIndex: string,
      endIndex: string,
      alterable: boolean,
      type: string
    ): Chainable<string>;
    createElementDialog(
      name: string,
      description: string,
      notes: string,
      startIndex: string,
      endIndex: string,
      alterable: boolean,
      type: string
    ): Chainable<JQuery<HTMLElement>>;
    elementRightClick(name: string): Chainable<JQuery<HTMLElement>>;
    editElementDescription(
      name: string,
      description: string
    ): Chainable<JQuery<HTMLElement>>;
    editElementNotes(
      name: string,
      notes: string
    ): Chainable<JQuery<HTMLElement>>;
    insertElementTop(associatedElement: string): Chainable<JQuery<HTMLElement>>;
    insertElementBottom(
      associatedElement: string
    ): Chainable<JQuery<HTMLElement>>;
    removeElement(associatedElement: string): Chainable<JQuery<HTMLElement>>;
    deleteElement(associatedElement: string): Chainable<JQuery<HTMLElement>>;
    validateElementHeaderExists(header: string): Chainable<JQuery<HTMLElement>>;
    validateFeatureInsideDialog(
      values: {
        title: string;
        description: string;
        valueType: string;
        multiValued: boolean;
        values: string[];
        defaultValue: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
      },
      includeFlags: boolean
    ): Chainable<JQuery<HTMLElement>>;

    editFeature(
      featureToEdit: string,
      previousValues: {
        title: string;
        description: string;
        valueType: string;
        multiValued: boolean;
        values: string[];
        defaultValue: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
      },
      newValues: {
        title: string;
        description: string;
        valueType: string;
        multiValued: boolean;
        values: string[];
        defaultValue: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
      }
    ): Chainable<JQuery<HTMLElement>>;
    openFeatureDropdown(): Chainable<JQuery<HTMLElement>>;
    addFeature(feature: {
      title: string;
      description: string;
      valueType?: string;
      multiValued?: boolean;
      values?: string[];
      defaultValue?: string;
      productTypes?: string[];
    }): Chainable<JQuery<HTMLElement>>;
    deleteFeature(feature: string): Chainable<JQuery<HTMLElement>>;
    validateFeatureExists(feature: string): Chainable<JQuery<HTMLElement>>;
    validateFeatureDoesNotExist(
      feature: string
    ): Chainable<JQuery<HTMLElement>>;
    changeApplicability(
      feature: string,
      featureIsMultiSelect: boolean,
      configOrGroup: string,
      wasValue: string | string[],
      isValue: string | string[]
    ): Chainable<JQuery<HTMLElement>>;
    editConfiguration(
      wasValue: {
        title: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
        groups: {
          name: string;
          enabled: boolean;
        }[];
      },
      isValue: {
        title: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
        groups: {
          name: string;
          enabled: boolean;
        }[];
      }
    ): Chainable<JQuery<HTMLElement>>;
    editConfigurationDropdown(
      wasValue: {
        title: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
        groups: {
          name: string;
          enabled: boolean;
        }[];
      },
      isValue: {
        title: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
        groups: {
          name: string;
          enabled: boolean;
        }[];
      }
    ): Chainable<JQuery<HTMLElement>>;
    editConfigurationClick(
      wasValue: {
        title: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
        groups: {
          name: string;
          enabled: boolean;
        }[];
      },
      isValue: {
        title: string;
        productTypes: {
          name: string;
          enabled: boolean;
        }[];
        groups: {
          name: string;
          enabled: boolean;
        }[];
      }
    ): Chainable<JQuery<HTMLElement>>;
    createConfiguration(value: {
      title: string;
      copyFrom: string;
      groups: string[];
      productTypes: {
        name: string;
        enabled: boolean;
      }[];
    }): Chainable<JQuery<HTMLElement>>;
    deleteConfiguration(name: string): Chainable<JQuery<HTMLElement>>;
    copyConfiguration(
      copyTo: string,
      copyFrom: string,
      expectedGroupCount: number
    ): Chainable<JQuery<HTMLElement>>;
    openConfigGroupDropdown(): Chainable<JQuery<HTMLElement>>;
    addConfigurationGroup(name: string): Chainable<JQuery<HTMLElement>>;
    syncConfigGroups(): Chainable<JQuery<HTMLElement>>;
    deleteConfigGroup(name: string): Chainable<JQuery<HTMLElement>>;
    removeConfigFromGroup(
      group: string,
      config: string
    ): Chainable<JQuery<HTMLElement>>;
    verifyGroups(
      groups: {
        name: string;
        enabled: boolean;
      }[]
    ): Chainable<JQuery<HTMLElement>>;
    editGroups(
      wasValues: {
        name: string;
        enabled: boolean;
      }[],
      isValues: {
        name: string;
        enabled: boolean;
      }[]
    ): Chainable<JQuery<HTMLElement>>;
    verifyProductTypes(
      values: {
        name: string;
        enabled: boolean;
      }[],
      includeFlags: boolean
    ): Chainable<JQuery<HTMLElement>>;
    editProductTypes(
      wasValues: {
        name: string;
        enabled: boolean;
      }[],
      isValues: {
        name: string;
        enabled: boolean;
      }[]
    ): Chainable<JQuery<HTMLElement>>;
    selectProductTypes(
      values: {
        name: string;
        enabled: boolean;
        currentState: boolean;
      }[]
    ): Chainable<JQuery<HTMLElement>>;
    validatePLConfigValues(
      columns: { title: string; values: { title: string; value: string }[] }[]
    ): Chainable<JQuery<HTMLElement>>;
    validateMIMValue(
      tableName: string,
      column: string,
      row: string,
      value: string,
      isLast?:boolean
    ): Chainable<JQuery<HTMLElement>>;
    undo(): Chainable<JQuery<HTMLElement>>;
    toggleBaseAddMenu(): Chainable<JQuery<HTMLElement>>;
    openNestedAddMenu(): Chainable<JQuery<HTMLElement>>;
    task<T>(
      event: string,
      arg?: any,
      options?: Partial<Loggable & Timeoutable>
    ): Chainable<T>;
  }
  interface NameResult {
    name: string;
  }
}
