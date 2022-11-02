/*********************************************************************
 * Copyright (c) 2021 Boeing
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
// Plugins enable you to tap into, modify, or extend the internal behavior of Cypress
// For more info, visit https://on.cypress.io/plugins-api
module.exports = (
	on: Cypress.PluginEvents,
	config: Cypress.PluginConfigOptions
) => {
	let latestBranchName = '';
	on('task', {
		setLatestBranchName(name: string) {
			latestBranchName = name;
			return null;
		},
		getLatestBranchName() {
			return latestBranchName;
		},
	});
};
