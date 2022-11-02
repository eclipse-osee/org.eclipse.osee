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
import { MarkdownModule } from 'ngx-markdown';
import { currentTextEditorServiceMock } from '../mocks/current-text-editor-service.mock';
import { CurrentTextEditorService } from '../services/current-text-editor.service';

import { ResizableSplitPaneCodeComponent } from './resizable-split-pane-code.component';

describe('ResizableSplitPaneCodeComponent', () => {
	let component: ResizableSplitPaneCodeComponent;
	let fixture: ComponentFixture<ResizableSplitPaneCodeComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				MatIconModule,
				MatButtonModule,
				MatInputModule,
				MatFormFieldModule,
				MatDividerModule,
				MarkdownModule.forRoot(),
				NoopAnimationsModule,
				FormsModule,
			],
			declarations: [ResizableSplitPaneCodeComponent],
			providers: [
				{
					provide: CurrentTextEditorService,
					useValue: currentTextEditorServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(ResizableSplitPaneCodeComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
