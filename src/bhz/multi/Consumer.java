package bhz.multi;

import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.WorkHandler;

public class Consumer implements WorkHandler<Order>{

	private String consumerId;
	
	private static AtomicInteger count = new AtomicInteger(0);
	
	public Consumer(String consumerId){
		this.consumerId = consumerId;
	}
	@Override
	public void onEvent(Order order) throws Exception {
		System.out.println("��ǰ�����ߣ�"+this.consumerId+"��������Ϣ��"+order.getId());
		count.incrementAndGet();
	}
	
	public int getCount(){
		return count.get();
	}
}
