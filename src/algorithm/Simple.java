package algorithm;

import java.util.List;

import struct.Input;
import struct.Order;
import struct.OrderLine;
import struct.Output;

public class Simple implements Algorithm {

	@Override
	public Output run(Input in) {
		Order o = in.getOrder();
		List<OrderLine> orderLines = o.getOrderLines();
		for (OrderLine ol : orderLines) {
			for (String barcode : ol.getBarcodes()) {
				System.out.println(barcode);
			}
		}
		return null;
	}
}
