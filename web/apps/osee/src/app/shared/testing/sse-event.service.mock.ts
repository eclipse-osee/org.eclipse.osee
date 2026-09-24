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
import { Subject } from 'rxjs';
import type { SseEventService } from '@osee/shared/services/network';

/**
 * Test double for {@link SseEventService}. It reports the server as ready (so the
 * {@link MutationService} chokepoint does not block mutations under test) and
 * exposes inert, Subject-backed event streams. Use it in any spec whose subject
 * (directly or transitively) mutates through the transaction/mutation chokepoint
 * or subscribes to SSE change streams, so the real Web Locks / EventSource
 * machinery never runs.
 */
export const sseEventServiceMock: Partial<SseEventService> = {
	serverReady: signal(true),
	connectionState: signal('connected'),
	isConnectionLeader: signal(true),
	sseConnectionId: '1',
	artifactChanges$: new Subject(),
	branchChanges$: new Subject(),
	presenceUpdates$: new Subject(),
	connectionReestablished$: new Subject(),
	connect: () => {},
	disconnect: () => {},
	reconnectNow: () => {},
};
