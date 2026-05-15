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
			storeType: 'String',
			multiplicityId: '1',
		},
		nativeContent: {
			id: '2',
			name: 'Native Content',
			value: '',
			typeId: ATTRIBUTETYPEIDENUM.NATIVE_CONTENT,
			storeType: 'Input Stream',
			multiplicityId: '1',
		},
		extension: {
			id: '3',
			name: 'Extension',
			value: 'txt',
			typeId: ATTRIBUTETYPEIDENUM.EXTENSION,
			storeType: 'String',
			multiplicityId: '3',
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
