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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CreateActionButtonComponent } from './create-action-button.component';
import { createActionServiceMock } from '@osee/configuration-management/testing';
import { CreateActionService } from '@osee/configuration-management/services';
import { BranchRoutedUIService } from '@osee/shared/services';
import { branchRoutedUiServiceMock } from '@osee/shared/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('CreateActionButtonComponent', () => {
	let component: CreateActionButtonComponent;
	let fixture: ComponentFixture<CreateActionButtonComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CreateActionButtonComponent],
			providers: [
				{
					provide: CreateActionService,
					useValue: createActionServiceMock,
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRoutedUiServiceMock,
				},
				{
					provide: ActivatedRoute,
					useValue: {
						queryParamMap: of(new Map<string, string>()),
					},
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CreateActionButtonComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
