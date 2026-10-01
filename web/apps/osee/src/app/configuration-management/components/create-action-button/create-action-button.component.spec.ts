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
import {
	actResultMock,
	createActionServiceMock,
} from '@osee/configuration-management/testing';
import { CreateActionService } from '@osee/configuration-management/services';
import { BranchRoutedUIService, UiService } from '@osee/shared/services';
import { branchRoutedUiServiceMock } from '@osee/shared/testing';
import { actionResult } from '@osee/shared/types/configuration-management';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('CreateActionButtonComponent', () => {
	let component: CreateActionButtonComponent;
	let fixture: ComponentFixture<CreateActionButtonComponent>;
	let createActionResult: actionResult;
	let branchRouter: { position: unknown };

	beforeEach(async () => {
		createActionResult = actResultMock;
		branchRouter = { ...branchRoutedUiServiceMock, position: 'unset' };

		await TestBed.configureTestingModule({
			imports: [CreateActionButtonComponent],
			providers: [
				{
					provide: CreateActionService,
					useValue: {
						...createActionServiceMock,
						createAction: () => of(createActionResult),
					},
				},
				{
					provide: BranchRoutedUIService,
					useValue: branchRouter,
				},
				{
					provide: MatDialog,
					useValue: {
						open: () => ({
							afterClosed: () => of({ description: 'x' }),
						}),
					},
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

	it('surfaces the failure reason and does not route on a 200-with-failure', () => {
		createActionResult = {
			...actResultMock,
			results: {
				...actResultMock.results,
				success: false,
				failed: true,
				results: ['Error: Invalid Parent Branch -1'],
			},
		};
		const uiService = TestBed.inject(UiService);
		const errorSpy = vi.spyOn(uiService, 'ErrorText', 'set');

		component.addAction();

		expect(errorSpy).toHaveBeenCalledWith(
			'Error: Invalid Parent Branch -1'
		);
		// Did not route to a (possibly invalid) branch.
		expect(branchRouter.position).toBe('unset');
	});
});
