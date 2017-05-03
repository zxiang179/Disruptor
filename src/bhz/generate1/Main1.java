package bhz.generate1;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.lmax.disruptor.BatchEventProcessor;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.YieldingWaitStrategy;

public class Main1 {
	
	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int BUFFER_SIZE = 1024;
		int THREAD_NUMBERS = 4;
		
		/**
		 * createSingleProducer����һ�����������ߵ�RingBuffer��
		 * ��һ��������EventFactory����������������¼���������ʵ����ְ����ǲ����������RingBuffer�����顣
		 * �ڶ���������RingBuffer�Ĵ�С����������2�������� Ŀ����Ϊ�˽���ģ����תΪ&�������Ч�ʡ�
		 * ������������RingBuffer����������û�п��������ʱ��(�����������ߣ�����˵���¼���������̫����)�ĵȴ����ԡ�
		 */
		final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {
			@Override
			public Trade newInstance() {
				return new Trade();
			}
		}, BUFFER_SIZE,new YieldingWaitStrategy());
		
		//�����̳߳�
		ExecutorService executors = Executors.newFixedThreadPool(THREAD_NUMBERS);
		//����SequenceBarrier
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
		//������Ϣ������
		BatchEventProcessor<Trade> transProcessor = 
				new BatchEventProcessor<Trade>(ringBuffer, sequenceBarrier, new TradeHandler());
		
		//��һ����Ŀ�ľ��ǰ������ߵ�λ����Ϣ����ע�뵽������ ���ֻ��һ�������ߵ��������ʡ��
		ringBuffer.addGatingSequences(transProcessor.getSequence());
		
		//����Ϣ�������ύ���̳߳�
		executors.submit(transProcessor);
		
		//ִ�������ߵĹ���
		//������ڶ�������� ���ظ�ִ����������д��� ��TradeHandler��������������
		Future<?> future = executors.submit(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				long seq;
				for(int i=0;i<10;i++){
					seq=ringBuffer.next();//
					ringBuffer.get(seq).setPrice(Math.random()*9999);//����������������
					ringBuffer.publish(seq);//����������������ʹhandler��consumer���ɼ�
				}
				return null;
			}
		});
		
		future.get();//�ȴ������߽���
		Thread.sleep(1000);//�ȴ�һ�룬�ȴ����Ѷ��������
		transProcessor.halt();//֪ͨ�¼� ����˵���������Խ�����
		executors.shutdown();//��ֹ�߳�
		
	}

}
