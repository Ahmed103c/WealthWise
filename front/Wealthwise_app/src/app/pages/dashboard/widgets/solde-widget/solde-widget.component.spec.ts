import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SoldeWidgetComponent } from './solde-widget.component';

describe('SoldeWidgetComponent', () => {
  let component: SoldeWidgetComponent;
  let fixture: ComponentFixture<SoldeWidgetComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SoldeWidgetComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SoldeWidgetComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
