import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IRoom, Room } from '../room.model';
import { RoomService } from '../service/room.service';

@Injectable({ providedIn: 'root' })
export class RoomRoutingResolveService implements Resolve<IRoom> {
  constructor(protected service: RoomService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<any> | Observable<never> {
    const returnData = {
      room: new Room(),
      facility_id_disable: false,
      facility_id: undefined,
    };
    const id = route.params['id'];
    if (route.queryParams['facility_id_disable'] === true || route.queryParams['facility_id_disable'] === 'true') {
      returnData.facility_id_disable = true;
    }
    if (route.queryParams['facility_id']) {
      returnData.facility_id = route.queryParams['facility_id'];
    }
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((room: HttpResponse<Room>) => {
          if (room.body) {
            returnData.room = room.body;
            return of(returnData);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(returnData);
  }
}
