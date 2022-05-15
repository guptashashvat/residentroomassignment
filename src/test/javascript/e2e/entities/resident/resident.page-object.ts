import { element, by, ElementFinder } from 'protractor';

export class ResidentComponentsPage {
  createButton = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('jhi-resident div table .btn-danger'));
  title = element.all(by.css('jhi-resident div h2#page-heading span')).first();
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

export class ResidentUpdatePage {
  pageTitle = element(by.id('jhi-resident-heading'));
  saveButton = element(by.id('save-entity'));
  cancelButton = element(by.id('cancel-save'));

  idInput = element(by.id('field_id'));
  nameInput = element(by.id('field_name'));
  phone_numberInput = element(by.id('field_phone_number'));
  emailInput = element(by.id('field_email'));

  roomSelect = element(by.id('field_room'));

  async getPageTitle(): Promise<string> {
    return this.pageTitle.getText();
  }

  async setIdInput(id: string): Promise<void> {
    await this.idInput.sendKeys(id);
  }

  async getIdInput(): Promise<string> {
    return await this.idInput.getAttribute('value');
  }

  async setNameInput(name: string): Promise<void> {
    await this.nameInput.sendKeys(name);
  }

  async getNameInput(): Promise<string> {
    return await this.nameInput.getAttribute('value');
  }

  async setPhone_numberInput(phone_number: string): Promise<void> {
    await this.phone_numberInput.sendKeys(phone_number);
  }

  async getPhone_numberInput(): Promise<string> {
    return await this.phone_numberInput.getAttribute('value');
  }

  async setEmailInput(email: string): Promise<void> {
    await this.emailInput.sendKeys(email);
  }

  async getEmailInput(): Promise<string> {
    return await this.emailInput.getAttribute('value');
  }

  async roomSelectLastOption(): Promise<void> {
    await this.roomSelect.all(by.tagName('option')).last().click();
  }

  async roomSelectOption(option: string): Promise<void> {
    await this.roomSelect.sendKeys(option);
  }

  getRoomSelect(): ElementFinder {
    return this.roomSelect;
  }

  async getRoomSelectedOption(): Promise<string> {
    return await this.roomSelect.element(by.css('option:checked')).getText();
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

export class ResidentDeleteDialog {
  private dialogTitle = element(by.id('jhi-delete-resident-heading'));
  private confirmButton = element(by.id('jhi-confirm-delete-resident'));

  async getDialogTitle(): Promise<string> {
    return this.dialogTitle.getText();
  }

  async clickOnConfirmButton(): Promise<void> {
    await this.confirmButton.click();
  }
}
