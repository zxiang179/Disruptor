package bhz.base;

import com.lmax.disruptor.EventFactory;

//��Ҫ��disruptorΪ���Ǵ����¼�������ͬʱ��������һ��EventFactory��ʵ����Event����
public class LongEventFactory implements EventFactory{

	@Override
	public Object newInstance() {
		return new LongEvent();
	}

}
