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
import { Component, input, model } from '@angular/core';
import { nodeData } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-persisted-subscriber-node-dropdown',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockPersistedSubscriberNodeDropdownComponent {
	artifactId = input.required<`${number}`>();
	connectionId = input.required<`${number}`>();
	nodes = model.required<nodeData[]>();
}
