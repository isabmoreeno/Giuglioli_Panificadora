export function totalForCustomer(customerId: number, productIds: number[], quantities: Record<string, number>): number {
  return productIds.reduce((sum, productId) => sum + Number(quantities[`${customerId}-${productId}`] || 0), 0);
}

export function totalForProduct(productId: number, customerIds: number[], quantities: Record<string, number>): number {
  return customerIds.reduce((sum, customerId) => sum + Number(quantities[`${customerId}-${productId}`] || 0), 0);
}
