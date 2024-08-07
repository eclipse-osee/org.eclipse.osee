/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { PlatformTypeCardComponent } from '@osee/messaging/shared/main-content';
import type { PlatformType } from '@osee/messaging/shared/types';

@Component({
	selector: 'osee-messaging-types-platform-type-card',
	template: '<p>Dummy</p>',
	standalone: true,
})
export class MockPlatformTypeCardComponent
	implements Partial<PlatformTypeCardComponent>
{
	@Input() typeData!: PlatformType;
}
