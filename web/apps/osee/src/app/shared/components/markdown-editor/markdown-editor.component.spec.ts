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

import { MarkdownEditorComponent } from './markdown-editor.component';
import { provideMarkdown } from 'ngx-markdown';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ArtifactExplorerHttpService } from '../../../ple/artifact-explorer/lib/services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../ple/artifact-explorer/lib/testing/artifact-explorer-http.service.mock';

describe('MarkdownEditorComponent', () => {
	let component: MarkdownEditorComponent;
	let fixture: ComponentFixture<MarkdownEditorComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [MarkdownEditorComponent, NoopAnimationsModule],
			providers: [
				provideMarkdown(),
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(MarkdownEditorComponent);
		component = fixture.componentInstance;
		fixture.componentRef.setInput('mdContent', 'this is a **bold**');
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
