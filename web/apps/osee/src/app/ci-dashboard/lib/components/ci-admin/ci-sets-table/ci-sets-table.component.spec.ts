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
import { CiSetsTableComponent } from './ci-sets-table.component';
import { CiSetsService } from '../../../services/ci-sets.service';
import { ciSetServiceMock } from '@osee/ci-dashboard/testing';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

describe('CiSetsTableComponent', () => {
	let component: CiSetsTableComponent;
	let fixture: ComponentFixture<CiSetsTableComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [CiSetsTableComponent],
			providers: [
				provideNoopAnimations(),
				{ provide: CiSetsService, useValue: ciSetServiceMock },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(CiSetsTableComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
