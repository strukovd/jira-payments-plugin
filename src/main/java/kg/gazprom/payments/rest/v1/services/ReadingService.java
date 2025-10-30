package kg.gazprom.payments.rest.v1.services;

import kg.gazprom.payments.models.db.ReadingDTO;
import kg.gazprom.payments.utils.pg;

import java.util.ArrayList;
import java.util.List;

public class ReadingService {
	public static List<ReadingDTO> getList(String account) {
		List<ReadingDTO> rows = new ArrayList<>();
		rows.add(
			new ReadingDTO(6, "040008472", 11, "2024-08-02 11:08:58.021000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(7, "040008472", 11, "2024-08-02 11:15:39.823000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(8, "040008472", 11, "2024-08-07 03:59:11.712000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(9, "000", 11, "2024-08-02 11:15:39.823000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(10, "000", 11, "2024-08-02 11:15:39.823000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(11, "000", 11, "2024-08-02 11:15:39.823000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(12, "000", 11, "2024-08-02 11:15:39.823000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(13, "000", 11, "2024-08-02 11:15:39.823000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(14, "000", 11, "2024-08-02 11:15:39.823000 +00:00", 24, 20, "+996771226554")
		);
		rows.add(
			new ReadingDTO(15, "000", 11, "2024-08-02 11:15:39.823000 +00:00", 24, 20, "+996771226554")
		);

		return rows;
	}

//	public static void changePaymentStatus(int invoiceId, boolean isPaid) {
//		pg.changePaymentStatus(invoiceId, isPaid);
//	}
//
	public static void delete(int id) {
		pg.removeReading(id);
	}
}
