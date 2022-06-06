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
import { links } from '../../../../../support/messaging/links';

links.forEach((link) => {
  describe(`Message View - ${link.name}`, () => {
    before(() => {
      cy.navigateToConnectionPage().navigateToMessagePage(link.name);
    });
    it('should enable MIM editing', () => {
      cy.intercept('GET', '/ats/action/**/*').as('action');
      cy.intercept('/ats/teamwf/**/*').as('teamwf');
      cy.intercept('/ats/config/teamdef/*/leads').as('leads');
      cy.intercept('/ats/ple/action/*/approval').as('approval');
      cy.enableMIMEditing().wait('@teamwf').wait('@leads').wait('@approval');
    });
  });
  link.messages?.forEach((message) => {
    message.subMessages?.forEach((submessage) => {
      describe(`${message.name}-Sub Message ${submessage.name} Functionality`, () => {
        it(`should create a submessage named ${submessage.name} linked to ${message.name}`, () => {
          cy.createSubMessage(
            message.name,
            submessage.name,
            submessage.description,
            submessage.subMessageNumber,
            message.nodeIsFirst
          )
            .get('mat-progress-bar')
            .should('not.exist');
        });
        it('should edit the description', () => {
          cy.editSubMessageTableDescription(
            message.name,
            submessage.name,
            submessage.editedDescription,
            message.nodeIsFirst
          );
        });
        it('should undo the description change', () => {
          cy.undo();
        });
        it('should insert a dummy submessage at bottom', () => {
          cy.insertSubMessageBottom(
            message.name,
            submessage.name,
            submessage.subMessageNumber,
            message.nodeIsFirst
          );
        });
        it('should delete the dummy submessage', () => {
          cy.deleteSubMessage(
            message.name,
            'Debug submessage',
            message.nodeIsFirst
          );
        });
        it('should insert a submessage at top', () => {
          cy.insertSubMessageTop(
            message.name,
            submessage.name,
            submessage.subMessageNumber,
            message.nodeIsFirst
          );
        });
        it('should remove dummy submessage', () => {
          cy.removeSubMessage(
            message.name,
            'Debug submessage',
            message.nodeIsFirst
          );
        });
        it('should validate submessage', () => {
          cy.openMessage(message.name, message.nodeIsFirst)
            .validateMIMValue(
              'submessage-table',
              'name',
              submessage.name,
              submessage.name
            )
            .validateMIMValue(
              'submessage-table',
              'description',
              submessage.name,
              submessage.description
            )
            .validateMIMValue(
              'submessage-table',
              'interfaceSubMessageNumber',
              submessage.name,
              submessage.subMessageNumber
            )
            .closeMessage(message.name, message.nodeIsFirst);
        });
      });
    });
  });
  describe(`Message View - ${link.name} Disable Editing`, () => {
    it('should disable MIM editing', () => {
      cy.disableMIMEditing();
    });
  });
});
