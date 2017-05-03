package bhz.base;

import java.nio.ByteBuffer;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.RingBuffer;

public class LongEventProducerWithTranslator {
	
	//一个translator可以看作一个事件的初始化器，publicEvent方法会调用它
	//填充Event
	public static final EventTranslatorOneArg<LongEvent, ByteBuffer> TRANSLATOR =
			new EventTranslatorOneArg<LongEvent, ByteBuffer>() {
				@Override
				public void translateTo(LongEvent event, long sequence,ByteBuffer buffer) {
					event.setValue(buffer.getLong(0));
				}
			};
			
	private final RingBuffer<LongEvent> ringBuffer;
	
	public LongEventProducerWithTranslator(RingBuffer<LongEvent> ringBuffer){
		this.ringBuffer=ringBuffer;
	}
	
	public void onData(ByteBuffer buffer){
		ringBuffer.publishEvent(TRANSLATOR,buffer);
	}
}
