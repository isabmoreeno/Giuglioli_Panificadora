import { describe, expect, it } from 'vitest';
import { totalForCustomer, totalForProduct } from './schedule-math';

describe('totais da programação', () => {
  const values = { '10-1': 20, '10-2': 5, '11-1': 7 };
  it('soma as quantidades de produtos para um cliente', () => {
    expect(totalForCustomer(10, [1, 2], values)).toBe(25);
  });
  it('soma as quantidades de clientes para um produto', () => {
    expect(totalForProduct(1, [10, 11], values)).toBe(27);
  });
});
