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
Cypress.Commands.add('undo', () => {
  cy.intercept('/orcs/branches/**/undo').as('undo');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('GET', '/ats/action/**/*').as('action');
  cy.intercept('/ats/teamwf/**/*').as('teamwf');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  cy.intercept('**/*/branch/**/*').as('branch')
  return cy
    .get('[data-cy=undo-btn]')
    .click()
    .wait('@leads')
    .wait('@undo')
    .wait('@branch')
    .wait('@action')
    .wait('@teamwf')
    .wait('@approval')
    .get('mat-progress-bar')
    .should('not.exist');
});
