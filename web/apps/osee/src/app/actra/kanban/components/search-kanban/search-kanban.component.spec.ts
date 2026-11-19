import { provideHttpClient } from '@angular/common/http';
import {
	HttpTestingController,
	provideHttpClientTesting,
} from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSelect } from '@angular/material/select';
import { By } from '@angular/platform-browser';
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

	it('loads team definitions and shows the other filters only after selection', async () => {
		const http = TestBed.inject(HttpTestingController);
		http.expectOne(apiURL + '/ats/config/teamdef').flush([
			{ id: '100', name: 'Peer Review' },
			{ id: '200', name: 'Code Review' },
		]);
		await fixture.whenStable();

		const select = fixture.debugElement.query(By.directive(MatSelect));
		const options = (select.componentInstance as MatSelect).options.toArray();
		expect(options.map(({ value, viewValue }) => [value, viewValue])).toEqual([
			['100', 'Peer Review'],
			['200', 'Code Review'],
		]);
		expect(fixture.debugElement.queryAll(By.directive(MatSelect))).toHaveLength(1);
		http.expectNone(apiURL + '/ats/world/my');

		select.triggerEventHandler('selectionChange', { value: '100' });
		TestBed.tick();
		for (const request of http.match(apiURL + '/ats/world/my')) {
			request.flush({ rows: [] });
		}
		http.verify();

		const filters = [...fixture.nativeElement.querySelectorAll('mat-select')];
		expect(filters).toHaveLength(4);
		expect(filters[0].id).toBe('team-definition-filter');
		expect(filters[1].id).toBe('team-filter');

		select.triggerEventHandler('selectionChange', { value: '' });
		TestBed.tick();
		expect(fixture.debugElement.queryAll(By.directive(MatSelect))).toHaveLength(1);
	});
});
