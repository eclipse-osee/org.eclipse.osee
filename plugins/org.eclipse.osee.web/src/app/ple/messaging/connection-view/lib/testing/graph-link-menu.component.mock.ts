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
import { Component, Input } from '@angular/core';
import type {
	connection,
	connectionWithChanges,
	OseeNode,
	node,
	nodeData,
	nodeDataWithChanges,
} from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-messaging-graph-link-menu',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockGraphLinkMenuComponent {
	@Input() editMode: boolean = false;
	@Input() data: connection | connectionWithChanges = {
		name: '',
		description: '',
		transportType: {
			name: 'ETHERNET',
			byteAlignValidation: false,
			byteAlignValidationSize: 0,
			messageGeneration: false,
			messageGenerationPosition: '',
			messageGenerationType: '',
		},
	};

	@Input()
	source!: OseeNode<node | nodeData | nodeDataWithChanges>;
	@Input()
	target!: OseeNode<node | nodeData | nodeDataWithChanges>;
}
