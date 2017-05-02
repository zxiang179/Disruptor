package bhz.base;

import java.nio.ByteBuffer;

import com.lmax.disruptor.RingBuffer;

/**
 * 很明显 当用一个简单队列来发布时间的时候会牵涉更多的细节，这是因为时间对象还需要预先创建。
 * 发布事件最少需要两步：获取下一个时间槽并发布事件（发布时间的时候要使用try/finally保证事件一定被发布）
 * 如果我们使用RingBuffer.next()获取一个事件槽，那么一定要发布对应的事件。
 * 如果不能发布事件，那么会引起Disruptor状态的混乱。
 * 尤其是在多个事件生产这的情况下会导致时间消费者失速，从而不得不重启应用才能恢复。
 * @author Carl_Hugo
 *
 */
public class LongEventProducer {
	
	private final RingBuffer<LongEvent> ringBuffer;
	
	public LongEventProducer(RingBuffer<LongEvent> ringBuffer){
		this.ringBuffer=ringBuffer;
	}
	
	/**
	 * onData用来发布事件，每调用一次就发布一次事件
	 * 它的参数会通过事件传递给消费者
	 * @param bb
	 */
	public void onData(ByteBuffer bb){
		//1 可以把ringBuffer看作一个时间队列那么next就是得到下一个事件槽
		long sequence = ringBuffer.next();
		try {
			//2 用上面的一个索引取出一个空的事件用于填充(获取该序号对应的事件对象)
			LongEvent longEvent = ringBuffer.get(sequence);
			//3 获取要通过事件传递的业务数据
			longEvent.setValue(bb.getLong(0));
		} finally{
			//4 发布事件
			//注意，最后的ringBuffer.publish方法必须包含在finally中以确保必须得到调用，如果某个请求的sequence未被提交
			ringBuffer.publish(sequence);	
		}
	}

}
