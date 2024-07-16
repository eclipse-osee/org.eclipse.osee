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
import { BranchPickerStub } from '@osee/shared/components/testing';
import { provideMarkdown } from 'ngx-markdown';
import { currentTextEditorServiceMock } from '../mocks/current-text-editor-service.mock';
import { ResizableSplitPaneCodeComponent } from '../resizable-split-pane-code/resizable-split-pane-code.component';
import { CurrentTextEditorService } from '../services/current-text-editor.service';

import { AsciidocEditorComponent } from './asciidoc-editor.component';
import { BranchPickerComponent } from '@osee/shared/components';
import { provideRouter } from '@angular/router';

describe('AsciidocEditorComponent', () => {
	let component: AsciidocEditorComponent;
	let fixture: ComponentFixture<AsciidocEditorComponent>;

	beforeEach(async () => {
		await TestBed.overrideComponent(AsciidocEditorComponent, {
			add: {
				imports: [BranchPickerStub, ResizableSplitPaneCodeComponent],
			},
			remove: {
				imports: [BranchPickerComponent],
			},
		})
			.configureTestingModule({
				imports: [
					MatIconModule,
					MatButtonModule,
					MatInputModule,
					MatFormFieldModule,
					MatDividerModule,
					NoopAnimationsModule,
					FormsModule,
					AsciidocEditorComponent,
				],
				declarations: [],
				providers: [
					provideMarkdown(),
					provideRouter([]),
					{
						provide: CurrentTextEditorService,
						useValue: currentTextEditorServiceMock,
					},
				],
			})
			.compileComponents();

		fixture = TestBed.createComponent(AsciidocEditorComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
