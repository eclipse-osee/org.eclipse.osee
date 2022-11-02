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
	describe(`Message View - ${link.name} Enable Editing`, () => {
		before(() => {
			cy.navigateToConnectionPage().navigateToMessagePage(link.name);
		});
		it('should enable MIM Editing', () => {
			cy.intercept('GET', '/ats/action/**/*').as('action');
			cy.intercept('GET', '/ats/teamwf/**/*').as('teamwf');
			cy.intercept('GET', '/ats/config/teamdef/*/leads').as('leads');
			cy.intercept('GET', '/ats/ple/action/*/approval').as('approval');
			cy.enableMIMEditing()
				.wait('@leads')
				.wait('@approval')
				.wait('@action')
				.get('mat-progress-bar')
				.should('not.exist');
		});
	});
	link.messages?.forEach((message) => {
		describe(`Message View -${message.name} Functionality`, () => {
			it(`should create a message named ${message.name} linked to ${link.name}`, () => {
				cy.createMessage(
					message.name,
					message.description,
					message.rate,
					message.periodicity,
					message.messageType,
					message.messageNumber,
					message.nodeIsFirst
				);
			});
			it('should edit the description', () => {
				cy.editMessageDescription(
					message.name,
					'Edited Description',
					message.nodeIsFirst
				);
			});
			it('should undo the edit', () => {
				cy.undo().wait(5000);
			});
			it('should validate everything is present', () => {
				cy.validateMIMValue(
					'message-table',
					'name',
					message.name,
					message.name,
					message.nodeIsFirst
				)
					.validateMIMValue(
						'message-table',
						'description',
						message.name,
						message.description,
						message.nodeIsFirst
					)
					.validateMIMValue(
						'message-table',
						'interfaceMessageNumber',
						message.name,
						message.messageNumber,
						message.nodeIsFirst
					)
					.validateMIMValue(
						'message-table',
						'interfaceMessagePeriodicity',
						message.name,
						message.periodicity,
						message.nodeIsFirst
					)
					.validateMIMValue(
						'message-table',
						'interfaceMessageRate',
						message.name,
						message.rate,
						message.nodeIsFirst
					)
					.validateMIMValue(
						'message-table',
						'interfaceMessageWriteAccess',
						message.name,
						'false',
						message.nodeIsFirst
					)
					.validateMIMValue(
						'message-table',
						'interfaceMessageType',
						message.name,
						message.messageType,
						message.nodeIsFirst
					);
			});
		});
	});
	describe(`Message View - ${link.name} Disable Editing`, () => {
		it('should disable MIM editing', () => {
			cy.disableMIMEditing();
		});
	});
});
