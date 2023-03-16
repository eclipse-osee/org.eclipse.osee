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
import { HarnessLoader } from '@angular/cdk/testing';
import { TestbedHarnessEnvironment } from '@angular/cdk/testing/testbed';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonHarness } from '@angular/material/button/testing';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';

import { currentBranchTransactionServiceMock } from '../internal/services/current-branch-transaction.service.mock';
import { CurrentBranchTransactionService } from '../internal/services/current-branch-transaction.service';

import { UndoButtonBranchComponent } from './undo-button-branch.component';

describe('UndoButtonBranchComponent', () => {
	let component: UndoButtonBranchComponent;
	let fixture: ComponentFixture<UndoButtonBranchComponent>;
	let loader: HarnessLoader;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatIconModule,
				MatButtonModule,
				MatTooltipModule,
				UndoButtonBranchComponent,
			],
			providers: [
				{
					provide: CurrentBranchTransactionService,
					useValue: currentBranchTransactionServiceMock,
				},
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(UndoButtonBranchComponent);
		component = fixture.componentInstance;
		loader = TestbedHarnessEnvironment.loader(fixture);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should undo a change', async () => {
		const spy = spyOn(component, 'undo').and.callThrough();
		await (await loader.getHarness(MatButtonHarness)).click();
		expect(spy).toHaveBeenCalled();
	});
});
