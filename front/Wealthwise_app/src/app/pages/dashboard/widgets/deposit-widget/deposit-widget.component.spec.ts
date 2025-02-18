import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepositWidgetComponent } from './deposit-widget.component';

describe('DepositWidgetComponent', () => {
  let component: DepositWidgetComponent;
  let fixture: ComponentFixture<DepositWidgetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepositWidgetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DepositWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
