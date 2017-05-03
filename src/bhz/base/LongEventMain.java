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
		//创建缓冲区
		ExecutorService executor = Executors.newCachedThreadPool();
		//创建工厂
		LongEventFactory factory = new LongEventFactory();
		//创建bufferSize，也就是RingBuffer的大小，必须是2的N次方
		int ringBufferSize = 1024*1024;
		
		/*//BlockingWaitStrategy是最低效的策略，但其对CPU的消耗最小并且在各种不同的环境中能够提供更加一致的性能表现
		WaitStrategy BLOCKING_WAIT = new BlockingWaitStrategy();
		//SleepingWaitStrategy的性能表现和BlockingWaitStrategy差不多，对CPU消耗也类似，但其对生产者线程的影响最小，适合用于异步日志类的场景。
		WaitStrategy SLEEPING_WAIT = new SleepingWaitStrategy();
		//YieldingWaitStrategy的性能最好，适用于低延迟的系统。在要求性能极高且时间处理线程数小于CPU逻辑线程数的场景中，推荐使用此策略：例如CPU开启超线程策略
		WaitStrategy YIELD_WAIT = new YieldingWaitStrategy();*/
		
		//创建Disruptor
		//1 第一个参数为工厂类对象，用于创建一个个的LongEvent，LongEvent是实际的消费数据
		//2 第二个参数是缓冲区大小
		//3 第三个参数是线程池，进行Disruptor内部的数据接受处理调度
		//4 第四个参数ProducerType.SINGLE和ProducerType.MULTI
		//5 第五个参数是一种策略:WaitStrategy
		Disruptor<LongEvent> disruptor = 
				new Disruptor<LongEvent>(factory, ringBufferSize, executor,ProducerType.SINGLE,new YieldingWaitStrategy());
		//连接消费事件方法
		disruptor.handleEventsWith(new LongEventHandler());
		//启动
		disruptor.start();
		
		//Disruptor的事件发布有两个阶段提交的过程
		//使用该方法获得具体存放数据的容器ringBuffer(环形结构)
		RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
		
//		LongEventProducer producer = new LongEventProducer(ringBuffer);
		LongEventProducerWithTranslator producer = new LongEventProducerWithTranslator(ringBuffer);
		ByteBuffer byteBuffer = ByteBuffer.allocate(8);
		for(long a=0;a<100;a++){
			byteBuffer.putLong(0,a);
			producer.onData(byteBuffer);
		}
		
		disruptor.shutdown();//关闭Disruptor，方法会堵塞，知道所有的事件都得到处理
		executor.shutdown();//关闭Disruptor使用的线程池：如果需要的话，必须手动关闭，Disruptor在shutdown时不会自动关闭。
	}

}
