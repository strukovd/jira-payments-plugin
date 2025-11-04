package kg.gazprom.payments.rest.v1.services;

import kg.gazprom.payments.models.db.ReadingDTO;
import kg.gazprom.payments.utils.pg;

import java.util.ArrayList;
import java.util.List;

public class ReadingService {
	public static List<ReadingDTO> getList(String account) {
		List<ReadingDTO> readings = pg.getReadings(account);

		return readings;
	}

//	public static void changePaymentStatus(int invoiceId, boolean isPaid) {
//		pg.changePaymentStatus(invoiceId, isPaid);
//	}
//
	public static void delete(int id) {
		pg.removeReading(id);
	}
}
