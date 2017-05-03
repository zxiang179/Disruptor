package bhz.generate2;

import bhz.generate1.Trade;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class Handler5 implements EventHandler<Trade>,WorkHandler<Trade>{

	@Override
	public void onEvent(Trade event) throws Exception {
		this.onEvent(event);
	}

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("Handler5 : getPrice :"+event.getPrice());
		event.setPrice(event.getPrice()+3.0);
	}

}