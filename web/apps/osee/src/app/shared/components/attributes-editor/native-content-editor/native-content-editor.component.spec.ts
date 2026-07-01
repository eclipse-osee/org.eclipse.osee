/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import {
	NativeContentEditorComponent,
	NativeEditorAttributes,
} from './native-content-editor.component';
import {
	BASEATTRIBUTETYPEIDENUM,
	ATTRIBUTETYPEIDENUM,
} from '@osee/attributes/constants';

describe('NativeContentEditorComponent', () => {
	let component: NativeContentEditorComponent;
	let fixture: ComponentFixture<NativeContentEditorComponent>;

	const mockNativeEditorAttrs: NativeEditorAttributes = {
		name: {
			id: '1',
			name: 'Name',
			value: 'test-file',
			typeId: BASEATTRIBUTETYPEIDENUM.NAME,
			gammaId: '-1',
			storeType: 'String',
		},
		nativeContent: {
			id: '2',
			name: 'Native Content',
			value: '',
			typeId: ATTRIBUTETYPEIDENUM.NATIVE_CONTENT,
			gammaId: '-1',
			storeType: 'Input Stream',
		},
		extension: {
			id: '3',
			name: 'Extension',
			value: 'txt',
			typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
			gammaId: '-1',
			storeType: 'String',
		},
	};

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [NativeContentEditorComponent],
			providers: [provideHttpClient(), provideHttpClientTesting()],
		}).compileComponents();

		fixture = TestBed.createComponent(NativeContentEditorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput(
			'nativeEditorAttributes',
			mockNativeEditorAttrs
		);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
