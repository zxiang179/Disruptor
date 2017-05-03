package bhz.generate1;

import java.util.UUID;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;

public class TradeHandler implements EventHandler<Trade>,WorkHandler<Trade>{

	@Override
	public void onEvent(Trade event) throws Exception {
		//这里做具体的消费逻辑
		event.setId(UUID.randomUUID().toString());//生成UUID
		System.out.print("id: "+ event.getId()+"  |  price:");
		System.out.println(event.getPrice());
	}

	@Override
	public void onEvent(Trade event, long sequence, boolean endOfBatch) throws Exception {
		this.onEvent(event);
	}

}
