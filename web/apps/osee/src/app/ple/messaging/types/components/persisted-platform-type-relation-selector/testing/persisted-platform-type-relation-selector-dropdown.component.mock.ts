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
import { Component, input, model, output } from '@angular/core';
import { PlatformType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-persisted-platform-type-relation-selector',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockPersistedPlatformTypeRelationSelectorComponent {
	artifactId = input.required<`${number}`>();
	platformType = model.required<PlatformType>();
	openContextMenu = output<MouseEvent>();
}
