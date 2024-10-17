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
import { Component, input } from '@angular/core';
import type {
	OseeEdge,
	connection,
	nodeData,
} from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-messaging-graph-node-menu',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockGraphNodeMenuComponent {
	public editMode = input.required<boolean>();
	public data = input.required<nodeData>();
	public sources = input.required<OseeEdge<connection>[]>();
	public targets = input.required<OseeEdge<connection>[]>();
}
