import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WithdrawlWidgetComponent } from './withdrawl-widget.component';

describe('WithdrawlWidgetComponent', () => {
  let component: WithdrawlWidgetComponent;
  let fixture: ComponentFixture<WithdrawlWidgetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WithdrawlWidgetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WithdrawlWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
