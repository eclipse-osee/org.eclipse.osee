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
import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { of, firstValueFrom } from 'rxjs';
import { ArtifactChangeNotificationService } from '../ple_aware/ui/event/artifact-change-notification.service';
import { BranchChangeEventService } from '../ple_aware/ui/event/branch-change-event.service';
import {
	MutationService,
	artifactChangeDescriptor,
	branchChangeDescriptor,
} from './mutation.service';
import { SseEventService } from './sse-event.service';

describe('MutationService', () => {
	let service: MutationService;
	let emitArtifact: ReturnType<typeof vi.fn>;
	let emitBranch: ReturnType<typeof vi.fn>;
	let serverReady: ReturnType<typeof signal<boolean>>;

	beforeEach(() => {
		emitArtifact = vi.fn();
		emitBranch = vi.fn();
		serverReady = signal(true);

		TestBed.configureTestingModule({
			providers: [
				MutationService,
				{
					provide: ArtifactChangeNotificationService,
					useValue: { emitLocalChange: emitArtifact },
				},
				{
					provide: BranchChangeEventService,
					useValue: { emitLocalChange: emitBranch },
				},
				{ provide: SseEventService, useValue: { serverReady } },
			],
		});
		service = TestBed.inject(MutationService);
	});

	const artifactDescriptor: artifactChangeDescriptor = {
		type: 'artifact',
		branchId: '570',
		artifactIds: ['11', '22'],
		transactionId: '999',
		changeTypes: ['attribute_modified'],
	};

	const branchDescriptor: branchChangeDescriptor = {
		type: 'branch',
		branchId: '570',
		changeType: 'committed',
		associatedArtifactId: '12345',
	};

	it('emits an artifact local change on a successful mutation', async () => {
		await firstValueFrom(
			service.mutateAndNotify(of({ ok: true }), () => artifactDescriptor)
		);
		expect(emitArtifact).toHaveBeenCalledWith(
			'570',
			['11', '22'],
			'999',
			['attribute_modified'],
			[],
			[]
		);
		expect(emitBranch).not.toHaveBeenCalled();
	});

	it('threads a descriptor changedAttributeTypeIds through to the local emit', async () => {
		await firstValueFrom(
			service.mutateAndNotify(of({ ok: true }), () => ({
				...artifactDescriptor,
				changedAttributeTypeIds: ['1152921504606847088'],
			}))
		);
		expect(emitArtifact).toHaveBeenCalledWith(
			'570',
			['11', '22'],
			'999',
			['attribute_modified'],
			[],
			['1152921504606847088']
		);
	});

	it('emits a branch local change for a branch descriptor', async () => {
		await firstValueFrom(
			service.mutateAndNotify(of({ ok: true }), () => branchDescriptor)
		);
		expect(emitBranch).toHaveBeenCalledWith('570', 'committed', '12345');
		expect(emitArtifact).not.toHaveBeenCalled();
	});

	it('emits nothing when describeChange returns null', async () => {
		await firstValueFrom(
			service.mutateAndNotify(of({ ok: true }), () => null)
		);
		expect(emitArtifact).not.toHaveBeenCalled();
		expect(emitBranch).not.toHaveBeenCalled();
	});

	it('does not emit an artifact change when artifactIds is empty', async () => {
		await firstValueFrom(
			service.mutateAndNotify(of({ ok: true }), () => ({
				...artifactDescriptor,
				artifactIds: [],
			}))
		);
		expect(emitArtifact).not.toHaveBeenCalled();
	});

	it('blocks the mutation when the server is not ready', async () => {
		serverReady.set(false);
		const describe = vi.fn(() => artifactDescriptor);

		await expect(
			firstValueFrom(service.mutateAndNotify(of({ ok: true }), describe))
		).rejects.toThrow(/not ready/i);

		// The HTTP call never runs (nothing to describe) and nothing is emitted.
		expect(describe).not.toHaveBeenCalled();
		expect(emitArtifact).not.toHaveBeenCalled();
	});

	it('re-evaluates readiness at subscribe time (defer)', async () => {
		// Build the observable while not ready...
		serverReady.set(false);
		const obs = service.mutateAndNotify(
			of({ ok: true }),
			() => artifactDescriptor
		);
		// ...then flip to ready before subscribing. defer() must see the latest value.
		serverReady.set(true);

		await firstValueFrom(obs);
		expect(emitArtifact).toHaveBeenCalled();
	});

	it('withLocalNotify emits without any readiness gate', async () => {
		serverReady.set(false); // gate is only in mutateAndNotify, not the raw operator
		await firstValueFrom(
			of({ ok: true }).pipe(
				service.withLocalNotify(() => branchDescriptor)
			)
		);
		expect(emitBranch).toHaveBeenCalledWith('570', 'committed', '12345');
	});
});
