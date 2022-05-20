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
Cypress.Commands.add(
  'subMessageDialogNew',
  (name: string, description: string, subMessageNumber: string) => {
    cy.intercept('orcs/txs').as('txs');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('/mim/branch/*/connections/*/messages/*').as('messages');
    return cy
      .get(`[data-cy="create-new-btn"]`)
      .click()
      .get(`[data-cy="field-name"]`)
      .focus()
      .type(name, { force: true })
      .get(`[data-cy="field-description"]`)
      .focus()
      .type(description, { force: true })
      .get(`[data-cy="field-sub-message-number"]`)
      .focus()
      .type(subMessageNumber, { force: true })
      .get(`[data-cy="stepper-next"]`)
      .click({ force: true })
      .get(`[data-cy="submit-btn"]`)
      .click()
      .wait('@txs')
      .wait('@messages')
      .wait('@action')
      .wait('@teamwf')
      .wait('@approval')
      .wait('@leads')
      .get('mat-progress-bar')
      .should('not.exist');
  }
);

Cypress.Commands.add(
  'createSubMessage',
  (
    associatedMessage: string,
    name: string,
    description: string,
    subMessageNumber,
    isMessageFirst
  ) => {
    cy.intercept('orcs/txs').as('txs');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    if (isMessageFirst) {
      return cy
        .get(`[data-cy="expand-message-btn-${associatedMessage}"]`)
        .first()
        .click()
        .openNestedAddMenu()
        .subMessageDialogNew(name, description, subMessageNumber)
        .toggleBaseAddMenu()
        .get(`[data-cy="close-message-btn-${associatedMessage}"]`)
        .click();
    } else {
      return cy
        .get(`[data-cy="expand-message-btn-${associatedMessage}"]`)
        .last()
        .click()
        .openNestedAddMenu()
        .subMessageDialogNew(name, description, subMessageNumber)
        .toggleBaseAddMenu()
        .get(`[data-cy="close-message-btn-${associatedMessage}"]`)
        .click();
    }
  }
);

Cypress.Commands.add(
  'editSubMessageTableDescription',
  (
    associatedMessage: string,
    name: string,
    description: string,
    isMessageFirst: boolean
  ) => {
    cy.intercept('orcs/txs').as('txs');
    cy.intercept('/mim/branch/*/connections/*/messages/filter/').as('messages');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    return cy
      .subMessageRightClick(associatedMessage, name, isMessageFirst)
      .get(`[data-cy="sub-msg-open-description-btn"]`)
      .click()
      .editFreeText(description)
      .wait('@txs')
      .wait('@messages')
      .get(`[data-cy="field-description"]`)
      .should('not.exist')
      .wait('@teamwf')
      .wait('@action')
      .wait('@approval')
      .wait('@leads')
      .closeMessage(associatedMessage, isMessageFirst);
  }
);

Cypress.Commands.add(
  'subMessageRightClick',
  (associatedMessage: string, submessage: string, isMessageFirst: boolean) => {
    cy.openMessage(associatedMessage, isMessageFirst);
    cy.get(`[data-cy="sub-message-table-row-${submessage}"]`).as('row');
    cy.get('@row').should('have.length.at.least', 1);
    return cy.get('@row').rightclick();
  }
);
Cypress.Commands.add(
  'insertSubMessageAfter',
  (
    associatedMessage: string,
    submessage: string,
    submessageOffset: string,
    isMessageFirst: boolean
  ) => {
    cy.subMessageRightClick(associatedMessage, submessage, isMessageFirst);
    cy.get(`[data-cy="sub-msg-insert-after-btn"]`).as('btn');
    return cy
      .get('@btn')
      .click()
      .subMessageDialogNew(
        'Debug submessage',
        'Debug submessage description',
        submessageOffset + 100
      )
      .closeMessage(associatedMessage, isMessageFirst);
  }
);

Cypress.Commands.add(
  'insertSubMessageBottom',
  (
    associatedMessage: string,
    submessage: string,
    submessageOffset: string,
    isMessageFirst: boolean
  ) => {
    cy.subMessageRightClick(associatedMessage, submessage, isMessageFirst);
    cy.get(`[data-cy="sub-msg-insert-end-btn"]`).as('btn');
    return cy
      .get('@btn')
      .click()
      .subMessageDialogNew(
        'Debug submessage',
        'Debug submessage description',
        submessageOffset + 200
      )
      .closeMessage(associatedMessage, isMessageFirst);
  }
);

Cypress.Commands.add(
  'insertSubMessageTop',
  (
    associatedMessage: string,
    submessage: string,
    submessageOffset: string,
    isMessageFirst: boolean
  ) => {
    cy.subMessageRightClick(associatedMessage, submessage, isMessageFirst);
    cy.get(`[data-cy="sub-msg-insert-top-btn"]`).as('btn');
    return cy
      .get('@btn')
      .click()
      .subMessageDialogNew(
        'Debug submessage',
        'Debug submessage description',
        submessageOffset + 200
      )
      .closeMessage(associatedMessage, isMessageFirst);
  }
);

Cypress.Commands.add(
  'deleteSubMessage',
  (associatedMessage: string, submessage: string, isMessageFirst: boolean) => {
    cy.intercept('orcs/txs').as('txs');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('/mim/branch/*/connections/*/messages/*').as('messages');
    cy.subMessageRightClick(associatedMessage, submessage, isMessageFirst);
    cy.get(`[data-cy="sub-msg-delete-btn"]`).as('btn');
    return cy
      .get('@btn')
      .click()
      .get('[data-cy=submit-btn]')
      .click()
      .wait('@txs')
      .wait('@messages')
      .wait('@action')
      .wait('@teamwf')
      .wait('@approval')
      .wait('@leads')
      .get('mat-progress-bar')
      .should('not.exist')
      .closeMessage(associatedMessage, isMessageFirst);
  }
);
Cypress.Commands.add(
  'removeSubMessage',
  (associatedMessage: string, submessage: string, isMessageFirst: boolean) => {
    cy.intercept('orcs/txs').as('txs');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('/mim/branch/*/connections/*/messages/*').as('messages');
    return cy
      .subMessageRightClick(associatedMessage, submessage, isMessageFirst)
      .get('[data-cy=sub-msg-remove-btn]')
      .click()
      .get('[data-cy=submit-btn]')
      .click()
      .wait('@txs')
      .wait('@messages')
      .wait('@action')
      .wait('@teamwf')
      .wait('@approval')
      .wait('@leads')
      .get('mat-progress-bar')
      .should('not.exist')
      .closeMessage(associatedMessage, isMessageFirst);
  }
);
