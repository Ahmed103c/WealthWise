import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepensePieChartComponent } from './depense-pie-chart.component';

describe('DepensePieChartComponent', () => {
  let component: DepensePieChartComponent;
  let fixture: ComponentFixture<DepensePieChartComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepensePieChartComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DepensePieChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
