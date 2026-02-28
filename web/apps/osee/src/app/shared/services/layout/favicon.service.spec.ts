import { TestBed } from '@angular/core/testing';

import { FaviconService } from './favicon.service';

describe('FaviconService', () => {
	let service: FaviconService;

	beforeEach(() => {
		TestBed.configureTestingModule({});
		service = TestBed.inject(FaviconService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});
});
