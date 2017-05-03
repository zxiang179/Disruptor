package bhz.generate2;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import bhz.generate1.Trade;

import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.EventFactory;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.EventHandlerGroup;
import com.lmax.disruptor.dsl.ProducerType;

public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		
		long beginTime = System.currentTimeMillis();
		int bufferSize =1024;
		ExecutorService executor = Executors.newFixedThreadPool(8);
		
		Disruptor<Trade> disruptor = new Disruptor<Trade>(new EventFactory<Trade>() {
			@Override
			public Trade newInstance() {
				return new Trade();
			}
		}, bufferSize, executor,ProducerType.SINGLE,new BusySpinWaitStrategy());
		
		//���β���
		//ʹ��disruptor������������C1��C2
		/*EventHandlerGroup<Trade> handlerGroup = 
				disruptor.handleEventsWith(new Handler1(),new Handler2());
		handlerGroup.then(new Handler3());*/
		
		//˳�����
		/*disruptor.handleEventsWith(new Handler1()).
			handleEventsWith(new Handler2()).
			handleEventsWith(new Handler3());*/
		
		//�����β���
		Handler1 h1 = new Handler1();
		Handler2 h2 = new Handler2();
		Handler3 h3 = new Handler3();
		Handler4 h4 = new Handler4();
		Handler5 h5 = new Handler5();
		disruptor.handleEventsWith(h1,h2);
		disruptor.after(h1).handleEventsWith(h4);
		disruptor.after(h2).handleEventsWith(h5);
		disruptor.after(h4,h5).handleEventsWith(h3);
		
		disruptor.start();//����
		CountDownLatch latch = new CountDownLatch(1);
		//������׼��
		executor.submit(new TradePublisher(latch,disruptor));
		//�ȴ�����������
		latch.await();
//		Thread.sleep(10);
		
		disruptor.shutdown();
		executor.shutdown();
		System.out.println("�ܺ�ʱ��"+(System.currentTimeMillis()-beginTime));
	}

}
