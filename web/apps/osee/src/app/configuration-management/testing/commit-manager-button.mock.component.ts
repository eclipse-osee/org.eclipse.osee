/*********************************************************************
 * Copyright (c) 2025 Boeing
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

@Component({
	selector: 'osee-commit-manager-button',
	template: '<div>Dummy</div>',
	standalone: true,
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class CommitManagerButtonStub {
	teamWorkflowId = input.required<`${number}`>();
}
