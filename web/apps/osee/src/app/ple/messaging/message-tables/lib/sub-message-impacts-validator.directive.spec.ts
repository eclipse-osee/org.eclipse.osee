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
import { TestBed } from '@angular/core/testing';
import { SubMessageImpactsValidatorDirective } from './sub-message-impacts-validator.directive';
import { CurrentMessagesService } from '@osee/messaging/shared/services';
import { CurrentMessageServiceMock } from '@osee/messaging/shared/testing';

describe('SubMessageImpactsValidatorDirective', () => {
	it('should create an instance', () => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: CurrentMessagesService,
					useValue: CurrentMessageServiceMock,
				},
			],
		});
		TestBed.runInInjectionContext(() => {
			const directive = new SubMessageImpactsValidatorDirective();
			expect(directive).toBeTruthy();
		});
	});
});
