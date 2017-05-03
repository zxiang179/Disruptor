package bhz.generate2;

import bhz.generate1.Trade;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class Handler4 implements EventHandler<Trade>,WorkHandler<Trade>{

	@Override
	public void onEvent(Trade event) throws Exception {
		this.onEvent(event);
	}

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("Handler4: getName :"+event.getName());
		event.setName(event.getName()+"h4");
	}

}
