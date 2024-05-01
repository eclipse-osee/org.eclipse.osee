/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { nodeData } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-new-node-form',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockNewNodeFormComponent {
	@Input() node!: nodeData;
}
