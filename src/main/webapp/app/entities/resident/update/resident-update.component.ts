import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IResident, Resident } from '../resident.model';
import { ResidentService } from '../service/resident.service';
import { IRoom } from 'app/entities/room/room.model';
import { RoomService } from 'app/entities/room/service/room.service';

@Component({
  selector: 'jhi-resident-update',
  templateUrl: './resident-update.component.html',
})
export class ResidentUpdateComponent implements OnInit {
  isSaving = false;

  roomsSharedCollection: IRoom[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    phone_number: [null, [Validators.required, Validators.max(9999999999999)]],
    email: [],
    room: [null, Validators.required],
  });

  constructor(
    protected residentService: ResidentService,
    protected roomService: RoomService,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ resident }) => {
      this.updateForm(resident.resident);

      if (resident.room_id_disable) {
        this.editForm.controls['room'].disable();
      }
      if (resident.room_id) {
        this.roomService.find(resident.room_id).subscribe(res => {
          this.editForm.controls['room'].setValue(res.body);
        });
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const resident = this.createFromForm();
    if (resident.id !== undefined) {
      this.subscribeToSaveResponse(this.residentService.update(resident));
    } else {
      this.subscribeToSaveResponse(this.residentService.create(resident));
    }
  }

  trackRoomById(_index: number, item: IRoom): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IResident>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(resident: IResident): void {
    this.editForm.patchValue({
      id: resident.id,
      name: resident.name,
      phone_number: resident.phone_number,
      email: resident.email,
      room: resident.room,
    });

    this.roomsSharedCollection = this.roomService.addRoomToCollectionIfMissing(this.roomsSharedCollection, resident.room);
  }

  protected loadRelationshipsOptions(): void {
    this.roomService
      .query()
      .pipe(map((res: HttpResponse<IRoom[]>) => res.body ?? []))
      .pipe(map((rooms: IRoom[]) => this.roomService.addRoomToCollectionIfMissing(rooms, this.editForm.get('room')!.value)))
      .subscribe((rooms: IRoom[]) => (this.roomsSharedCollection = rooms));
  }

  protected createFromForm(): IResident {
    return {
      ...new Resident(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      phone_number: this.editForm.get(['phone_number'])!.value,
      email: this.editForm.get(['email'])!.value,
      room: this.editForm.get(['room'])!.value,
    };
  }
}
