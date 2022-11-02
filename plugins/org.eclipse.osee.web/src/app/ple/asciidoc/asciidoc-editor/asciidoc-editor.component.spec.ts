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
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { RouterTestingModule } from '@angular/router/testing';
import { MarkdownModule } from 'ngx-markdown';
import { BranchPickerStub } from 'src/app/shared-components/components/branch-picker/branch-picker/branch-picker.mock.component';
import { currentTextEditorServiceMock } from '../mocks/current-text-editor-service.mock';
import { ResizableSplitPaneCodeComponent } from '../resizable-split-pane-code/resizable-split-pane-code.component';
import { CurrentTextEditorService } from '../services/current-text-editor.service';

import { AsciidocEditorComponent } from './asciidoc-editor.component';

describe('AsciidocEditorComponent', () => {
	let component: AsciidocEditorComponent;
	let fixture: ComponentFixture<AsciidocEditorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				RouterTestingModule,
				MatIconModule,
				MatButtonModule,
				MatInputModule,
				MatFormFieldModule,
				MatDividerModule,
				MarkdownModule.forRoot(),
				NoopAnimationsModule,
				FormsModule,
			],
			declarations: [
				AsciidocEditorComponent,
				ResizableSplitPaneCodeComponent,
				BranchPickerStub,
			],
			providers: [
				{
					provide: CurrentTextEditorService,
					useValue: currentTextEditorServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(AsciidocEditorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
