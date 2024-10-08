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
import { CurrentStateServiceMock } from '@osee/messaging/shared/testing';
import { STRUCTURE_SERVICE_TOKEN } from '@osee/messaging/shared/tokens';
import { StructureImpactsValidatorDirective } from './structure-impacts-validator.directive';

describe('StructureImpactsValidatorDirective', () => {
	it('should create an instance', () => {
		TestBed.configureTestingModule({
			providers: [
				{
					provide: STRUCTURE_SERVICE_TOKEN,
					useValue: CurrentStateServiceMock,
				},
			],
		});
		TestBed.runInInjectionContext(() => {
			const directive = new StructureImpactsValidatorDirective();
			expect(directive).toBeTruthy();
		});
	});
});
