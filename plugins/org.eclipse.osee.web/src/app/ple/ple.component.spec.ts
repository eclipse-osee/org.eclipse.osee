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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatButtonModule } from '@angular/material/button';
import { Router, ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PleComponent } from './ple.component';

describe('PleComponent', () => {
	let component: PleComponent;
	let fixture: ComponentFixture<PleComponent>;
	let router: any;

	beforeEach(async () => {
		router = jasmine.createSpyObj('Router', ['navigate']);
		await TestBed.configureTestingModule({
			imports: [MatButtonModule, RouterTestingModule],
			declarations: [PleComponent],
			providers: [
				{ provide: Router, useValue: router },
				{ provide: ActivatedRoute, useValue: {} },
			],
		}).compileComponents();
	});

	beforeEach(() => {
		fixture = TestBed.createComponent(PleComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should navigate to plconfig', () => {
		component.navigateTo('plconfig');
		expect(router.navigate).toHaveBeenCalledWith(['plconfig'], {
			relativeTo: undefined,
			queryParamsHandling: 'merge',
		});
		//expect(component).toBeTruthy();
	});
});
