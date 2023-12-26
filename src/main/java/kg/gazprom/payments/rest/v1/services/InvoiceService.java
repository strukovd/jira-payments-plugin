package kg.gazprom.payments.rest.v1.services;

import kg.gazprom.payments.utils.pg;

public class InvoiceService {
    public static void changePaymentStatus(int invoiceId, boolean isPaid) {
        pg.changePaymentStatus(invoiceId, isPaid);
    }

    public static void changeInvoiceReqAmount(int id, double reqAmount) {
        pg.changeInvoiceReqAmount(id, reqAmount);
    }
}
