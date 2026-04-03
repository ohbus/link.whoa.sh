import 'zone.js';
import 'zone.js/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AnimatedCounterComponent } from './animated-counter';
import { Component, signal } from '@angular/core';
import { describe, it, expect, beforeEach, vi } from 'vitest';

@Component({
  standalone: true,
  imports: [AnimatedCounterComponent],
  template: `<app-animated-counter [value]="targetValue()"></app-animated-counter>`,
})
class TestHostComponent {
  targetValue = signal(0);
}

describe('AnimatedCounterComponent', () => {
  let component: AnimatedCounterComponent;
  let fixture: ComponentFixture<TestHostComponent>;
  let hostComponent: TestHostComponent;

  beforeEach(async () => {
    vi.useFakeTimers();
    await TestBed.configureTestingModule({
      imports: [AnimatedCounterComponent, TestHostComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TestHostComponent);
    hostComponent = fixture.componentInstance;
  });

  it('should animate from 0 to target value', async () => {
    const componentFixture = TestBed.createComponent(AnimatedCounterComponent);
    component = componentFixture.componentInstance;

    // Set initial value
    componentFixture.componentRef.setInput('value', 100);
    componentFixture.detectChanges();

    // Initially it should be 0
    expect(component.displayValue()).toBe(0);

    vi.advanceTimersByTime(300);
    componentFixture.detectChanges();
    expect(component.displayValue()).toBeGreaterThan(0);
    expect(component.displayValue()).toBeLessThan(100);

    vi.advanceTimersByTime(1000);
    componentFixture.detectChanges();
    expect(component.displayValue()).toBe(100);
  });

  it('should handle decreasing values', async () => {
    const componentFixture = TestBed.createComponent(AnimatedCounterComponent);
    component = componentFixture.componentInstance;

    // Set initial
    componentFixture.componentRef.setInput('value', 100);
    componentFixture.detectChanges();
    vi.advanceTimersByTime(2000);
    expect(component.displayValue()).toBe(100);

    // Decrease
    componentFixture.componentRef.setInput('value', 50);
    componentFixture.detectChanges();

    vi.advanceTimersByTime(2000);
    componentFixture.detectChanges();
    expect(component.displayValue()).toBe(50);
  });
});
