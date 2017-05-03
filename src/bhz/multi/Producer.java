package bhz.multi;

import com.lmax.disruptor.RingBuffer;

public class Producer {
	
	private final RingBuffer<Order> ringBuffer;
	
	public Producer(RingBuffer<Order> ringBuffer){
		this.ringBuffer=ringBuffer;
	}
	
	/**
	 * onData���������¼���ÿ����һ�ξͷ���һ���¼�
	 * ���Ĳ�����ͨ���¼����ݸ�������
	 * @param data
	 */
	public void onData(String data){
		//���԰�ringBuffer������һ���¼����У���ônext���ǵõ���һ���¼���
		long sequence = ringBuffer.next();
		try {
			Order order = ringBuffer.get(sequence);
			order.setId(data);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//�����¼�
			ringBuffer.publish(sequence);
		}
	}
}
