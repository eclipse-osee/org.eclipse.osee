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
// ***********************************************************
// This example support/index.js is processed and
// loaded automatically before your test files.
//
// This is a great place to put global configuration and
// behavior that modifies Cypress.
//
// You can change the location of this file or turn off
// automatically serving support files with the
// 'supportFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/configuration
// ***********************************************************

// When a command from ./commands is ready to use, import with `import './commands'` syntax
import './commands';
import './branch.commands';
import './messaging/platform.types.commands';
import './messaging/mim.preferences.commands';
import './messaging/node.commands';
import './messaging/connection.commands';
import './undo.commands';
import './messaging/message.commands';
import './messaging/submessage.commands';
import './freetext.commands';
import './messaging/structure.commands';
import './messaging/element.commands';
import './messaging/value.commands';
import './messaging/transport.types.commands';
import './nested-add-button.commands';
import './plconfig/feature.commands';
import './plconfig/config.commands';
import './plconfig/product.types.commands';
import './plconfig/group.commands';
import './plconfig/values.commands';
import '@jscutlery/cypress-harness/support';

Cypress.Screenshot.defaults({
  disableTimersAndAnimations: false,
});
