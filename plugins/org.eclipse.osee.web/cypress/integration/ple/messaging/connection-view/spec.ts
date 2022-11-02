import { links } from '../../../../support/messaging/links';
import { nodes } from '../../../../support/messaging/nodes';

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
describe('Connection View Setup', () => {
	before(() => {
		cy.navigateToConnectionPage();
	});
	beforeEach(() => {
		cy.intercept('/mim/branch/*/graph').as('graph');
		cy.intercept('GET', '/ats/action/**/*').as('action');
		cy.intercept('/ats/teamwf/**/*').as('teamwf');
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
	});
	it('should enable editing', () => {
		cy.enableMIMEditing().wait('@approval').wait('@graph');
	});
});

nodes.forEach((node) => {
	describe(`Node Functionality - ${node.name}`, () => {
		it(`should create a new node named ${node.name}`, () => {
			cy.createNode(
				node.name,
				node.description,
				node.color,
				node.address
			);
		});
		it('should delete the node', () => {
			cy.deleteNode(node.name);
		});
		it('should undo the deletion', () => {
			cy.undo();
		});
	});
});

links.forEach((link) => {
	describe(`Edge Functionality - ${link.name}`, () => {
		it(`should create a connection named ${link.name}`, () => {
			cy.createConnection(
				link.fromNode,
				link.toNode,
				link.name,
				link.description,
				link.transportType
			);
		});
		/**
		 * Currently, relation deletion doesn't undo properly
		 */
		it(`should delete a connection named ${link.name}`, () => {
			cy.deleteConnection(link.name);
		});
		it('should undo the deletion', () => {
			cy.undo().undo();
		});
	});
});
describe('Connection View Cleanup', () => {
	it('should disable editing', () => {
		cy.disableMIMEditing();
	});
});
