import { browser, ExpectedConditions as ec /* , promise */ } from 'protractor';
import { NavBarPage, SignInPage } from '../../page-objects/jhi-page-objects';

import {
  RoomComponentsPage,
  /* RoomDeleteDialog, */
  RoomUpdatePage,
} from './room.page-object';

const expect = chai.expect;

describe('Room e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let roomComponentsPage: RoomComponentsPage;
  let roomUpdatePage: RoomUpdatePage;
  /* let roomDeleteDialog: RoomDeleteDialog; */
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.autoSignInUsing(username, password);
    await browser.wait(ec.visibilityOf(navBarPage.entityMenu), 5000);
  });

  it('should load Rooms', async () => {
    await navBarPage.goToEntity('room');
    roomComponentsPage = new RoomComponentsPage();
    await browser.wait(ec.visibilityOf(roomComponentsPage.title), 5000);
    expect(await roomComponentsPage.getTitle()).to.eq('Rooms');
    await browser.wait(ec.or(ec.visibilityOf(roomComponentsPage.entities), ec.visibilityOf(roomComponentsPage.noResult)), 1000);
  });

  it('should load create Room page', async () => {
    await roomComponentsPage.clickOnCreateButton();
    roomUpdatePage = new RoomUpdatePage();
    expect(await roomUpdatePage.getPageTitle()).to.eq('Create or edit a Room');
    await roomUpdatePage.cancel();
  });

  /* it('should create and save Rooms', async () => {
        const nbButtonsBeforeCreate = await roomComponentsPage.countDeleteButtons();

        await roomComponentsPage.clickOnCreateButton();

        await promise.all([
            roomUpdatePage.setRoom_numberInput('5'),
            roomUpdatePage.facilitySelectLastOption(),
        ]);

        await roomUpdatePage.save();
        expect(await roomUpdatePage.getSaveButton().isPresent(), 'Expected save button disappear').to.be.false;

        expect(await roomComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeCreate + 1, 'Expected one more entry in the table');
    }); */

  /* it('should delete last Room', async () => {
        const nbButtonsBeforeDelete = await roomComponentsPage.countDeleteButtons();
        await roomComponentsPage.clickOnLastDeleteButton();

        roomDeleteDialog = new RoomDeleteDialog();
        expect(await roomDeleteDialog.getDialogTitle())
            .to.eq('Are you sure you want to delete this Room?');
        await roomDeleteDialog.clickOnConfirmButton();
        await browser.wait(ec.visibilityOf(roomComponentsPage.title), 5000);

        expect(await roomComponentsPage.countDeleteButtons()).to.eq(nbButtonsBeforeDelete - 1);
    }); */

  after(async () => {
    await navBarPage.autoSignOut();
  });
});
