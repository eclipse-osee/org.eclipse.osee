/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import { Component, computed, input, model } from '@angular/core';
import { NodeSearchComponent } from '@osee/messaging/nodes/search';
import { nodeData } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-node-search',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockNodeSearchComponent implements Partial<NodeSearchComponent> {
	selectedNode = model.required<nodeData>();
	protectedNodes = input.required<nodeData[]>();
}
