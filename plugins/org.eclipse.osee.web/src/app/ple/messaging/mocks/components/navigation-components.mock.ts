/* eslint-disable @angular-eslint/component-class-suffix */
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

@Component({
	selector: 'osee-mock-help',
	template: '<p>Dummy</p>',
})
export class MessagingHelpDummy {}

@Component({
	selector: 'osee-mock-main',
	template: '<p>Dummy</p>',
})
export class MessagingMainMock {}

@Component({
	selector: 'osee-mock-type-search',
	template: '<p>Dummy</p>',
})
export class MessagingTypeSearchMock {}
