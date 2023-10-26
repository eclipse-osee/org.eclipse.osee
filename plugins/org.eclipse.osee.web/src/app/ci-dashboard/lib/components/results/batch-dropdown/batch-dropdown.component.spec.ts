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
import { BatchDropdownComponent } from './batch-dropdown.component';
import { TmoHttpService } from '../../../services/tmo-http.service';
import { tmoHttpServiceMock } from '../../../services/tmo-http.service.mock';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('BatchDropdownComponent', () => {
	let component: BatchDropdownComponent;
	let fixture: ComponentFixture<BatchDropdownComponent>;

	beforeEach(() => {
		TestBed.configureTestingModule({
			imports: [BatchDropdownComponent, NoopAnimationsModule],
			providers: [
				{ provide: TmoHttpService, useValue: tmoHttpServiceMock },
			],
		});
		fixture = TestBed.createComponent(BatchDropdownComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
