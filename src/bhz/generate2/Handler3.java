package bhz.generate2;

import bhz.generate1.Trade;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class Handler3 implements EventHandler<Trade>{

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		System.out.println("handler3: name: "+event.getName()+
				" , price: "+event.getPrice()+
				" ; instance: "+event.toString());
	}

}
