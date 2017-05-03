package bhz.generate1;

import java.util.UUID;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class TradeHandler implements EventHandler<Trade>,WorkHandler<Trade>{

	@Override
	public void onEvent(Trade event) throws Exception {
		//����������������߼�
		event.setId(UUID.randomUUID().toString());//����UUID
		System.out.print("id: "+ event.getId()+"  |  price:");
		System.out.println(event.getPrice());
	}

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		this.onEvent(event);
	}

}
