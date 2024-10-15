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
import { PlatformTypeSentinel } from '@osee/messaging/shared/enumerations';
import { PlatformType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-platform-type-dropdown',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockPlatformTypeDropdownComponent {
	allowOpenInSameTab = input(false);
	extraPlatformTypes = input<PlatformType[]>([]);
	disabled = input(false);
	required = input(false);
	hideSearchButton = input(false);
	contextmenu = output<MouseEvent>();
	platformType = model<PlatformType>(new PlatformTypeSentinel('None'));
}
