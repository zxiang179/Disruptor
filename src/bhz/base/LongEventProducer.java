package bhz.base;

import java.nio.ByteBuffer;

import com.lmax.disruptor.RingBuffer;

/**
 * ������ ����һ���򵥶���������ʱ���ʱ���ǣ������ϸ�ڣ�������Ϊʱ�������ҪԤ�ȴ�����
 * �����¼�������Ҫ��������ȡ��һ��ʱ��۲������¼�������ʱ���ʱ��Ҫʹ��try/finally��֤�¼�һ����������
 * �������ʹ��RingBuffer.next()��ȡһ���¼��ۣ���ôһ��Ҫ������Ӧ���¼���
 * ������ܷ����¼�����ô������Disruptor״̬�Ļ��ҡ�
 * �������ڶ���¼������������»ᵼ��ʱ��������ʧ�٣��Ӷ����ò�����Ӧ�ò��ָܻ���
 * @author Carl_Hugo
 *
 */
public class LongEventProducer {
	
	private final RingBuffer<LongEvent> ringBuffer;
	
	public LongEventProducer(RingBuffer<LongEvent> ringBuffer){
		this.ringBuffer=ringBuffer;
	}
	
	/**
	 * onData���������¼���ÿ����һ�ξͷ���һ���¼�
	 * ���Ĳ�����ͨ���¼����ݸ�������
	 * @param bb
	 */
	public void onData(ByteBuffer bb){
		//1 ���԰�ringBuffer����һ��ʱ�������ônext���ǵõ���һ���¼���
		long sequence = ringBuffer.next();
		try {
			//2 �������һ������ȡ��һ���յ��¼��������(��ȡ����Ŷ�Ӧ���¼�����)
			LongEvent longEvent = ringBuffer.get(sequence);
			//3 ��ȡҪͨ���¼����ݵ�ҵ������
			longEvent.setValue(bb.getLong(0));
		} finally{
			//4 �����¼�
			//ע�⣬����ringBuffer.publish�������������finally����ȷ������õ����ã����ĳ�������sequenceδ���ύ
			ringBuffer.publish(sequence);	
		}
	}

}
