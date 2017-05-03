package bhz.base;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class LongEventMain {
	
	public static void main(String[] args) {
		//����������
		ExecutorService executor = Executors.newCachedThreadPool();
		//��������
		LongEventFactory factory = new LongEventFactory();
		//����bufferSize��Ҳ����RingBuffer�Ĵ�С��������2��N�η�
		int ringBufferSize = 1024*1024;
		
		/*//BlockingWaitStrategy�����Ч�Ĳ��ԣ������CPU��������С�����ڸ��ֲ�ͬ�Ļ������ܹ��ṩ����һ�µ����ܱ���
		WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
		//SleepingWaitStrategy�����ܱ��ֺ�BlockingWaitStrategy��࣬��CPU����Ҳ���ƣ�������������̵߳�Ӱ����С���ʺ������첽��־��ĳ�����
		WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
		//YieldingWaitStrategy��������ã������ڵ��ӳٵ�ϵͳ����Ҫ�����ܼ�����ʱ�䴦���߳���С��CPU�߼��߳����ĳ����У��Ƽ�ʹ�ô˲��ԣ�����CPU�������̲߳���
		WaitStrategy YIELD_WAIT = new YieldingWaitStrategy();*/
		
		//����Disruptor
		//1 ��һ������Ϊ������������ڴ���һ������LongEvent��LongEvent��ʵ�ʵ���������
		//2 �ڶ��������ǻ�������С
		//3 �������������̳߳أ�����Disruptor�ڲ������ݽ��ܴ������
		//4 ���ĸ�����ProducerType.SINGLE��ProducerType.MULTI
		//5 �����������һ�ֲ���:WaitStrategy
		Disruptor<LongEvent> disruptor = 
				new Disruptor<LongEvent>(factory, ringBufferSize, executor,ProducerType.SINGLE,new YieldingWaitStrategy());
		//���������¼�����
		disruptor.handleEventsWith(new LongEventHandler());
		//����
		disruptor.start();
		
		//Disruptor���¼������������׶��ύ�Ĺ���
		//ʹ�ø÷�����þ��������ݵ�����ringBuffer(���νṹ)
		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		
//		LongEventProducer producer = new LongEventProducer(ringBuffer);
		LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		for(long a=0;a<100;a++){
			byteBuffer.putLong(0,a);
			producer.onData(byteBuffer);
		}
		
		disruptor.shutdown();//�ر�Disruptor�������������֪�����е��¼����õ�����
		executor.shutdown();//�ر�Disruptorʹ�õ��̳߳أ������Ҫ�Ļ��������ֶ��رգ�Disruptor��shutdownʱ�����Զ��رա�
	}

}
