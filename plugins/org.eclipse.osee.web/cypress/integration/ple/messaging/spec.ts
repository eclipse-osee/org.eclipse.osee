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
describe('Visit messages page', () => {
  it('Visits the page', () => {
    cy.visit('/ple').get('[data-cy="messaging-nav-button"]').click();
  });

  it('should contain title', () => {
    cy.get('.mat-display-4').should('contain.text', 'OSEE Messaging');
  });

  it('should have 6 buttons', () => {
    cy.get('.messaging-grid').find('button').its('length').should('eq', 6);
  });
});
