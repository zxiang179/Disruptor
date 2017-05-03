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
		 * createSingleProducer创建一个单例生产者的RingBuffer。
		 * 第一个参数交EventFactory。从名字上理解是事件工厂，其实它的职责就是产生数据填充RingBuffer的区块。
		 * 第二个参数是RingBuffer的大小，它必须是2的整数倍 目的是为了将求模运算转为&运算提高效率。
		 * 第三个参数是RingBuffer的生产者在没有可用区块的时候(可能是消费者（或者说是事件处理器）太慢了)的等待策略。
		 */
		final RingBuffer<Trade> ringBuffer = RingBuffer.createSingleProducer(new EventFactory<Trade>() {
			@Override
			public Trade newInstance() {
				return new Trade();
			}
		}, BUFFER_SIZE,new YieldingWaitStrategy());
		
		//创建线程池
		ExecutorService executors = Executors.newFixedThreadPool(THREAD_NUMBERS);
		//创建SequenceBarrier
		SequenceBarrier sequenceBarrier = ringBuffer.newBarrier();
		//创建消息处理器
		BatchEventProcessor<Trade> transProcessor = 
				new BatchEventProcessor<Trade>(ringBuffer, sequenceBarrier, new TradeHandler());
		
		//这一步的目的就是把消费者的位置信息引用注入到生产者 如果只有一个消费者的情况可以省略
		ringBuffer.addGatingSequences(transProcessor.getSequence());
		
		//把消息处理器提交到线程池
		executors.submit(transProcessor);
		
		//执行生产者的工作
		//如果存在多个消费者 那重复执行上面的三行代码 把TradeHandler换成其他消费者
		Future<?> future = executors.submit(new Callable<Void>(){
			@Override
			public Void call() throws Exception {
				long seq;
				for(int i=0;i<10;i++){
					seq=ringBuffer.next();//
					ringBuffer.get(seq).setPrice(Math.random()*9999);//给这个区块放入数据
					ringBuffer.publish(seq);//发布这个区块的数据使handler（consumer）可见
				}
				return null;
			}
		});
		
		future.get();//等待生产者结束
		Thread.sleep(1000);//等待一秒，等待消费都处理结束
		transProcessor.halt();//通知事件 或者说处理器可以结束了
		executors.shutdown();//终止线程
		
	}

}
