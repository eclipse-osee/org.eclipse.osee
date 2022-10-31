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
Cypress.Commands.add('navigateToConnectionPage', () => {
  cy.intercept('/mim/branch/*/graph').as('graph');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.visit('/ple')
      .get('[data-cy="messaging-nav-button"]')
      .click()
      .get('[data-cy="connection-nav-button"]')
      .click()
      .task<Cypress.NameResult>('getLatestBranchName')
      .then((branchname) => {
        const branch: string = branchname.name;
        return cy.selectBranch(branch, 'working').wait('@graph').wait('@action').wait('@teamwf').wait('@approval').wait('@leads');
      });
  return cy.get('mat-progress-bar').should('not.exist');
})
Cypress.Commands.add(
  'createConnection',
  (
    fromNode: string,
    toNode: string,
    name: string,
    description: string,
    transportType: string
  ) => {
    cy.intercept('POST','orcs/txs').as('txs');
    cy.intercept('/mim/branch/*/graph').as('graph');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('GET', '/mim/branch/*/transportTypes').as('transports');
    return cy
      .get(`[data-cy="node-${fromNode}"`)
      .rightclick()
      .get('[data-cy="create-connection-btn"]')
      .click()
      .wait('@transports')
      .get('[data-cy=field-name]')
      .focus()
      .type(name)
      .get('[data-cy=field-description]')
      .focus()
      .type(description)
      .get('[data-cy=field-transport-type]')
      .click()
      .get('[data-cy=mat-option-loading-spinner]')
      .should('not.exist')
      .get(`[data-cy=option-${transportType}]`)
      .click()
      .get('[data-cy=field-toNode]')
      .click()
      .get('[data-cy=mat-option-loading-spinner]')
      .should('not.exist')
      .get(`[data-cy=option-${toNode}]`)
      .click()
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
Cypress.Commands.add('deleteConnection', (name: string) => {
  cy.intercept('POST','orcs/txs').as('txs');
  cy.intercept('/mim/branch/*/graph').as('graph');
  return cy
    .get(`[data-cy=link-${name}]`)
    .rightclick({force:true})
    .get('[data-cy=delete-connection-btn')
    .click()
    .get('[data-cy=submit-btn]')
    .click()
    .wait('@txs')
    .wait('@graph')
    .get('mat-progress-bar')
    .should('not.exist');
});
