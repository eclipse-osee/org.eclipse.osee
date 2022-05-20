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
  'createNode',
  (name: string, description: string, color: string, address: string) => {
    cy.intercept('orcs/txs').as('txs');
    cy.intercept('/mim/branch/*/graph').as('graph');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    return cy
      .get('[data-cy="graph"]')
      .rightclick()
      .get('[data-cy="create-new-node-btn"]')
      .click()
      .get('[data-cy=field-name]')
      .focus()
      .type(name)
      .get('[data-cy=field-description]')
      .focus()
      .type(description)
      .get('input[type=color]')
      .invoke('val', color)
      .trigger('change')
      .get('[data-cy=field-address]')
      .focus()
      .type(address)
      .get('[data-cy=submit-btn]')
      .click()
      .wait('@txs')
      .wait('@graph')
      .wait('@action')
      .wait('@teamwf')
      .wait('@approval')
      .wait('@leads')
      .get('mat-progress-bar')
      .should('not.exist');
  }
);

Cypress.Commands.add('deleteNode', (name: string) => {
  cy.intercept('orcs/txs').as('txs');
  cy.intercept('/mim/branch/*/graph').as('graph');
  cy.intercept('GET', '/ats/action/**/*').as('action');
  cy.intercept('/ats/teamwf/**/*').as('teamwf');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  return cy
    .get(`[data-cy="node-${name}"`)
    .rightclick()
    .get('[data-cy=delete-node-btn]')
    .click()
    .get('[data-cy=submit-btn]')
    .click()
    .wait('@txs')
    .wait('@graph')
    .wait('@teamwf')
    .wait('@action')
    .wait('@leads')
    .wait('@approval')
    .get('mat-progress-bar')
    .should('not.exist');
});
