import { provideHttpClient } from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { ActionService } from '@osee/configuration-management/services';
import { apiURL } from '@osee/environments';
import { of } from 'rxjs';

import { SearchKanbanComponent } from './search-kanban.component';

describe('SearchKanbanComponent', () => {
	let fixture: ComponentFixture<SearchKanbanComponent>;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			imports: [SearchKanbanComponent, NoopAnimationsModule],
			providers: [
				provideHttpClient(),
				provideHttpClientTesting(),
				{ provide: ActionService, useValue: { users: of([]) } },
			],
		}).compileComponents();

		fixture = TestBed.createComponent(SearchKanbanComponent);
		fixture.detectChanges();
		TestBed.tick();
	});

	it('shows Team Definition before the existing filters', () => {
		const http = TestBed.inject(HttpTestingController);
		http.expectOne(apiURL + '/ats/config/teamdef').flush([]);
		for (const request of http.match(apiURL + '/ats/world/my')) {
			request.flush({ rows: [] });
		}
		http.verify();

		const filters = [...fixture.nativeElement.querySelectorAll('mat-select')];
		expect(filters).toHaveLength(4);
		expect(filters[0].id).toBe('team-definition-filter');
		expect(filters[1].id).toBe('team-filter');
	});
});
