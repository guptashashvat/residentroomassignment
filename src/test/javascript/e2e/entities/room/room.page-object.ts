import { element, by, ElementFinder } from 'protractor';

export class RoomComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-room div table .btn-danger'));
  title = element.all(by.css('jhi-room div h2#page-heading span')).first();
  noResult = element(by.id('no-result'));
  entities = element(by.id('entities'));

  async clickOnCreateButton(): Promise<void> {
    await this.createButton.click();
  }

  async clickOnLastDeleteButton(): Promise<void> {
    await this.deleteButtons.last().click();
  }

  async countDeleteButtons(): Promise<number> {
    return this.deleteButtons.count();
  }

  async getTitle(): Promise<string> {
    return this.title.getText();
  }
}

export class RoomUpdatePage {
  pageTitle = element(by.id('jhi-room-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  idInput = element(by.id('field_id'));
  room_numberInput = element(by.id('field_room_number'));

  facilitySelect = element(by.id('field_facility'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getText();
  }

  async setIdInput(id: string): Promise<void> {
    await this.idInput.sendKeys(id);
  }

  async getIdInput(): Promise<string> {
    return await this.idInput.getAttribute('value');
  }

  async setRoom_numberInput(room_number: string): Promise<void> {
    await this.room_numberInput.sendKeys(room_number);
  }

  async getRoom_numberInput(): Promise<string> {
    return await this.room_numberInput.getAttribute('value');
  }

  async facilitySelectLastOption(): Promise<void> {
    await this.facilitySelect.all(by.tagName('option')).last().click();
  }

  async facilitySelectOption(option: string): Promise<void> {
    await this.facilitySelect.sendKeys(option);
  }

  getFacilitySelect(): ElementFinder {
    return this.facilitySelect;
  }

  async getFacilitySelectedOption(): Promise<string> {
    return await this.facilitySelect.element(by.css('option:checked')).getText();
  }

  async save(): Promise<void> {
    await this.saveButton.click();
  }

  async cancel(): Promise<void> {
    await this.cancelButton.click();
  }

  getSaveButton(): ElementFinder {
    return this.saveButton;
  }
}

export class RoomDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-room-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-room'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
