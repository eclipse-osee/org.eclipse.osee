import { types } from './types';

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
  'createElementDialog',
  (
    name: string,
    description: string,
    notes: string,
    startIndex: string,
    endIndex: string,
    alterable: boolean,
    type: string
  ) => {
    cy.intercept('orcs/txs').as('txs');
    cy.intercept(
      '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
    ).as('structures');
    cy.intercept('/mim/branch/*/types').as('types');
    cy.get(`[data-cy="create-new-btn"]`).click();
    cy.get(`[data-cy="field-name"]`).as('name');
    cy.get(`[data-cy="field-description"]`).as('description');
    cy.get(`[data-cy="field-notes"]`).as('notes');
    cy.get(`[data-cy="field-index-start"]`).as('start');
    cy.get(`[data-cy="field-index-end"]`).as('end');
    name.split('').forEach((character) => {
      cy.get('@name').focus().type(character);
    });
    cy.get('@name').focus().should('have.value', name);
    cy.get(`@description`).focus().type(description).should('have.value', description);
    cy.get(`@notes`).focus().type(notes).should('have.value', notes);
    cy.get(`@start`).focus().type(startIndex);
    cy.get(`@end`).focus().type(endIndex);
    cy.get(`[data-cy="search-type-menu"]`)
      .click({ force: true })
      .get('mat-dialog-content')
      .first()
      .click({force:true})
      .get(`[data-cy="field-logical-type"]`)
      .click({force:true})
      .get(`[data-cy="option-${type}"]`)
      .click()
      .get('mat-dialog-content')
      .first()
      .click({force:true})
      .get(`[data-cy="query-button"]`)
      .click({ force: true })
      .get(`[data-cy="stepper-next"]`)
      .click({ force: true });
    return cy
      .get(`[data-cy="submit-btn"]`)
      .click({force:true})
      .wait('@txs')
      .wait('@structures')
      .get('osee-messaging-add-element-dialog')
      .should('not.exist');
  }
);
Cypress.Commands.add('elementRightClick', (name: string) => {
  cy.get(`[data-cy="element-table-row-${name}"] > .cdk-column-description`).as(
    'row'
  );
  cy.get('@row').should('have.length.at.least', 1);
  return cy.get('@row').should('not.be.hidden').rightclick();
});
Cypress.Commands.add(
  'createElement',
  (
    name: string,
    description: string,
    notes: string,
    startIndex: string,
    endIndex: string,
    alterable: boolean,
    type: string
  ) => {
    return cy
      .openNestedAddMenu()
      .createElementDialog(
        name,
        description,
        notes,
        startIndex,
        endIndex,
        alterable,
        type
      )
      .toggleBaseAddMenu()
      .get('mat-progress-bar')
      .should('not.exist')
      .url()
      .should('not.include', '#');
  }
);
Cypress.Commands.add(
  'editElementDescription',
  (name: string, description: string) => {
    cy.intercept('orcs/txs').as('txs');
    cy.intercept(
      '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
    ).as('structures');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    return cy
      .elementRightClick(name)
      .get(`[data-cy="element-open-description-btn"]`)
      .click()
      .editFreeText(description)
      .wait('@txs')
      .wait('@structures')
      .wait('@action')
      .wait('@leads')
      .wait('@teamwf')
      .wait('@approval')
      .get(`[data-cy="field-description"]`)
      .should('not.exist');
  }
);
Cypress.Commands.add('editElementNotes', (name: string, notes: string) => {
  cy.intercept('orcs/txs').as('txs');
  cy.intercept(
    '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
  ).as('structures');
  cy.intercept('GET', '/ats/action/**/*').as('action');
  cy.intercept('/ats/teamwf/**/*').as('teamwf');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  return cy
    .elementRightClick(name)
    .get(`[data-cy="element-open-notes-btn"]`)
    .click()
    .editFreeText(notes)
    .wait('@txs')
    .wait('@structures')
    .wait('@action')
    .wait('@leads')
    .wait('@teamwf')
    .wait('@approval')
    .get(`[data-cy="field-description"]`)
    .should('not.exist');
});

Cypress.Commands.add('insertElementBottom', (associatedElement: string) => {
  cy.elementRightClick(associatedElement);
  cy.get(`[data-cy="element-insert-end-btn"]`).as('btn');
  return cy
    .get('@btn')
    .click()
    .createElementDialog(
      'Debug element Bottom',
      'Debug Element Description',
      'Debug Element notes',
      '0',
      '0',
      true,
      types[0]
    );
});

Cypress.Commands.add('insertElementTop', (associatedElement: string) => {
  cy.elementRightClick(associatedElement);
  cy.get(`[data-cy="element-insert-top-btn"]`).as('btn');
  return cy
    .get('@btn')
    .click()
    .createElementDialog(
      'Debug element Top',
      'Debug Element Description',
      'Debug Element notes',
      '0',
      '0',
      true,
      types[0]
    );
});
Cypress.Commands.add('removeElement', (associatedElement: string) => {
  cy.elementRightClick(associatedElement);
  cy.get('[data-cy=element-remove-btn]').as('btn');
  cy.intercept('GET', '/ats/action/**/*').as('action');
  cy.intercept('/ats/teamwf/**/*').as('teamwf');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  cy.intercept('orcs/txs').as('txs');
  return cy
    .get('@btn')
    .click()
    .get('[data-cy=submit-btn]')
    .click()
    .wait('@txs')
    .wait('@teamwf')
    .wait('@action')
    .get('mat-progress-bar')
    .should('not.exist');
});
Cypress.Commands.add('deleteElement', (associatedElement: string) => {
  cy.elementRightClick(associatedElement);
  cy.get('[data-cy=element-delete-btn]').as('btn');
  cy.intercept('GET', '/ats/action/**/*').as('action');
  cy.intercept('/ats/teamwf/**/*').as('teamwf');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  cy.intercept('orcs/txs').as('txs');
  return cy
    .get('@btn')
    .click()
    .get('[data-cy=submit-btn]')
    .click()
    .wait('@txs')
    .wait('@teamwf')
    .wait('@action')
    .get('mat-progress-bar')
    .should('not.exist');
});
Cypress.Commands.add('validateElementHeaderExists', (header: string) => {
  cy.get(`[data-cy=element-table-header-${header}]`)
    .scrollIntoView()
    .should('exist');
});
