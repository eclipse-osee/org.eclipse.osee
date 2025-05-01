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

import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { publishMarkdownDialogDataMock } from '../../../../../../lib/testing/artifact-explorer.data.mock';
import { ArtifactExplorerHttpService } from '../../../../../services/artifact-explorer-http.service';
import { ArtifactExplorerHttpServiceMock } from '../../../../../testing/artifact-explorer-http.service.mock';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { PublishMarkdownAsHtmlDialogComponent } from './publish-markdown-as-html-dialog.component';

describe('PublishMarkdownAsHtmlDialogComponent', () => {
	let component: PublishMarkdownAsHtmlDialogComponent;
	let fixture: ComponentFixture<PublishMarkdownAsHtmlDialogComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [
				PublishMarkdownAsHtmlDialogComponent,
				NoopAnimationsModule,
			],
			providers: [
				{ provide: MatDialogRef, useValue: {} },
				{
					provide: MAT_DIALOG_DATA,
					useValue: publishMarkdownDialogDataMock,
				},
				{
					provide: ArtifactExplorerHttpService,
					useValue: ArtifactExplorerHttpServiceMock,
				},
			],
		}).compileComponents();

		fixture = TestBed.createComponent(PublishMarkdownAsHtmlDialogComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});
});
