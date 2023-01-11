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
import { TestBed } from '@angular/core/testing';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of } from 'rxjs';
import { TestScheduler } from 'rxjs/testing';
import { UiService } from '../../../../../ple-services/ui/ui.service';
import {
	affectedArtifactHttpServiceMock,
	affectedArtifactHttpServiceWithWarningResultsMock,
	warningArtifacts,
} from '../../testing/affected-artifact.service.mock';
import { AffectedArtifactService } from '../http/affected-artifact.service';

import { WarningDialogService } from './warning-dialog.service';

describe('WarningDialogService', () => {
	let service: WarningDialogService;
	let scheduler: TestScheduler;
	let uiService: UiService;

	beforeEach(
		() =>
			(scheduler = new TestScheduler((actual, expected) => {
				expect(actual).toEqual(expected);
			}))
	);

	describe('Cases where Dialog should not open', () => {
		beforeEach(() => {
			TestBed.configureTestingModule({
				imports: [MatDialogModule],
				providers: [
					{
						provide: AffectedArtifactService,
						useValue: affectedArtifactHttpServiceMock,
					},
				],
			});
			service = TestBed.inject(WarningDialogService);
			uiService = TestBed.inject(UiService);
		});

		beforeEach(() => {
			uiService.idValue = '10';
		});

		it('should be created', () => {
			expect(service).toBeTruthy();
		});

		it('should return submessage', () => {
			scheduler.run(({ expectObservable }) => {
				const body = { id: '20', name: 'submessage0' };
				expectObservable(service.openSubMessageDialog(body)).toBe(
					'(a|)',
					{ a: body }
				);
			});
		});

		it('should return structure', () => {
			scheduler.run(({ expectObservable }) => {
				const body = { id: '20', name: 'structure0' };
				expectObservable(service.openStructureDialog(body)).toBe(
					'(a|)',
					{ a: body }
				);
			});
		});

		it('should return element', () => {
			scheduler.run(({ expectObservable }) => {
				const body = { id: '20', name: 'element0' };
				expectObservable(service.openElementDialog(body)).toBe('(a|)', {
					a: body,
				});
			});
		});
	});

	describe('Cases where Dialog should be open', () => {
		beforeEach(() => {
			TestBed.configureTestingModule({
				imports: [MatDialogModule, NoopAnimationsModule],
				providers: [
					{
						provide: AffectedArtifactService,
						useValue:
							affectedArtifactHttpServiceWithWarningResultsMock,
					},
				],
			});
			service = TestBed.inject(WarningDialogService);
			uiService = TestBed.inject(UiService);
		});

		beforeEach(() => {
			uiService.idValue = '10';
		});

		it('should be created', () => {
			expect(service).toBeTruthy();
		});

		it('should return submessage', () => {
			scheduler.run(({ expectObservable }) => {
				const body = { id: '20', name: 'submessage0' };
				let dialogRefSpy = jasmine.createSpyObj({
					afterClosed: of({
						affectedArtifacts: warningArtifacts,
						body: body,
						modifiedObjectType: 'SubMessage',
						affectedArtifactType: 'Message',
					}),
					close: null,
				});
				let dialogSpy = spyOn(
					TestBed.inject(MatDialog),
					'open'
				).and.returnValue(dialogRefSpy);
				expectObservable(service.openSubMessageDialog(body)).toBe(
					'(a|)',
					{ a: body }
				);
			});
		});

		it('should return structure', () => {
			scheduler.run(({ expectObservable }) => {
				const body = { id: '20', name: 'structure0' };
				let dialogRefSpy = jasmine.createSpyObj({
					afterClosed: of({
						affectedArtifacts: warningArtifacts,
						body: body,
						modifiedObjectType: 'Structure',
						affectedArtifactType: 'SubMessage',
					}),
					close: null,
				});
				let dialogSpy = spyOn(
					TestBed.inject(MatDialog),
					'open'
				).and.returnValue(dialogRefSpy);
				expectObservable(service.openStructureDialog(body)).toBe(
					'(a|)',
					{ a: body }
				);
			});
		});

		it('should return element', () => {
			scheduler.run(({ expectObservable }) => {
				const body = { id: '20', name: 'element0' };
				let dialogRefSpy = jasmine.createSpyObj({
					afterClosed: of({
						affectedArtifacts: warningArtifacts,
						body: body,
						modifiedObjectType: 'Element',
						affectedArtifactType: 'Structure',
					}),
					close: null,
				});
				let dialogSpy = spyOn(
					TestBed.inject(MatDialog),
					'open'
				).and.returnValue(dialogRefSpy);
				expectObservable(service.openElementDialog(body)).toBe('(a|)', {
					a: body,
				});
			});
		});
	});
});
