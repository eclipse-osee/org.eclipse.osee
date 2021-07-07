import { Injectable } from '@angular/core';
import { from, of } from 'rxjs';
import { filter, share } from 'rxjs/operators';
import { data } from '../mock/mock-graph-data'

@Injectable({
  providedIn: 'root'
})
export class GraphService {

  constructor () { }
  
  getNodes(id: string) {
    return from(data).pipe(
      filter((val) => val.id === id),
    )
  }
}
