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
import { TestBed } from '@angular/core/testing';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { OSEEAuthURL } from '@osee/environments';
import { user } from '@osee/shared/types/auth';
import { UserDataAccountService } from './user-data-account.service';

/**
 * DEV-scheme auth resolution. The test build uses the base environment (`authScheme: 'DEV'`), so
 * these exercise the DEV branch of UserDataAccountService directly.
 *
 * The contract:
 * - No `osee.account.id` seeded -> the hardcoded dev user (id 61106791), NO server call. This keeps
 *   ordinary single-user local/e2e runs authenticated with zero setup.
 * - `osee.account.id` seeded (Playwright `newUserPage` does this) -> fetch THAT user from the
 *   server, so multi-user flows (presence, "changed by another user") use distinct identities.
 * - Fetch fails -> fall back to the hardcoded dev user, so a bad/misseeded id never wedges the app.
 *
 * `_devAuth` reads localStorage when the service is constructed, so each test seeds (or clears)
 * localStorage BEFORE injecting the service.
 */
describe('UserDataAccountService (DEV auth resolution)', () => {
	const DEV_USER_ID = '61106791' as const;
	const SEEDED_ID = '3333' as const;

	function makeUser(id: `${number}`): user {
		return {
			id,
			name: `User ${id}`,
			guid: null,
			active: true,
			description: null,
			workTypes: [],
			tags: [],
			userId: id,
			email: '',
			loginIds: [],
			savedSearches: [],
			userGroups: [],
			artifactId: '',
			idString: id,
			idIntValue: Number(id),
			uuid: Number(id),
			roles: [],
		};
	}

	let http: HttpTestingController;

	function inject(): UserDataAccountService {
		TestBed.configureTestingModule({
			providers: [
				UserDataAccountService,
				provideHttpClient(),
				provideHttpClientTesting(),
			],
		});
		http = TestBed.inject(HttpTestingController);
		return TestBed.inject(UserDataAccountService);
	}

	beforeEach(() => {
		TestBed.resetTestingModule();
		localStorage.removeItem('osee.account.id');
	});

	afterEach(() => {
		localStorage.removeItem('osee.account.id');
		http.verify();
	});

	it('returns the hardcoded dev user with NO server call when no account is seeded', async () => {
		const service = inject();
		const resolved = await firstValueFrom(service.user);
		expect(resolved.id).toBe(DEV_USER_ID);
		// No request to the auth endpoint in the unseeded dev path.
		http.expectNone(OSEEAuthURL);
	});

	it('fetches the seeded user from the server when osee.account.id is set', async () => {
		localStorage.setItem('osee.account.id', SEEDED_ID);
		const service = inject();

		const pending = firstValueFrom(service.user);
		const req = http.expectOne(OSEEAuthURL);
		// The request asserts the seeded identity via the headers the interceptor would send.
		expect(req.request.method).toBe('GET');
		expect(req.request.headers.get('Authorization')).toBe(SEEDED_ID);
		expect(req.request.headers.get('osee.account.id')).toBe(SEEDED_ID);
		req.flush(makeUser(SEEDED_ID));

		const resolved = await pending;
		expect(resolved.id).toBe(SEEDED_ID);
	});

	it('falls back to the hardcoded dev user when the seeded fetch fails', async () => {
		localStorage.setItem('osee.account.id', SEEDED_ID);
		const service = inject();

		const pending = firstValueFrom(service.user);
		const req = http.expectOne(OSEEAuthURL);
		req.flush('boom', { status: 500, statusText: 'Server Error' });

		const resolved = await pending;
		expect(resolved.id).toBe(DEV_USER_ID);
	});
});
