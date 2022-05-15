import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IResident, Resident } from '../resident.model';
import { ResidentService } from '../service/resident.service';

@Injectable({ providedIn: 'root' })
export class ResidentRoutingResolveService implements Resolve<IResident> {
  constructor(protected service: ResidentService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<any> | Observable<never> {
    const returnData = {
      resident: new Resident(),
      room_id_disable: false,
      room_id: undefined,
    };
    const id = route.params['id'];
    if (route.queryParams['room_id_disable'] === true || route.queryParams['room_id_disable'] === 'true') {
      returnData.room_id_disable = true;
    }
    if (route.queryParams['room_id']) {
      returnData.room_id = route.queryParams['room_id'];
    }
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((resident: HttpResponse<Resident>) => {
          if (resident.body) {
            returnData.resident = resident.body;
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
