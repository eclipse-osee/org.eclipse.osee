/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { SetDropdownMultiComponent } from './set-dropdown-multi.component';
import { TmoHttpService } from '../../../services/tmo-http.service';
import { tmoHttpServiceMock } from '../../../services/tmo-http.service.mock';
import { CiSetsHttpService } from '../../../services/ci-sets-http.service';
import { ciSetsHttpServiceMock } from '../../../services/ci-sets-http.service.mock';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('SetDropdownMultiComponent', () => {
	let component: SetDropdownMultiComponent;
	let fixture: ComponentFixture<SetDropdownMultiComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [SetDropdownMultiComponent, NoopAnimationsModule],
			providers: [
				{ provide: TmoHttpService, useValue: tmoHttpServiceMock },
				{ provide: CiSetsHttpService, useValue: ciSetsHttpServiceMock },
			],
		});
		fixture = TestBed.createComponent(SetDropdownMultiComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
